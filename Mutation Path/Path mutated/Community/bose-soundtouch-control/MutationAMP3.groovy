
definition(
    name:"Bose®SoundTouch®Control",
    namespace:"smartthings",
    author:"SmartThings&Joe Geiger",
    description:"Control your Bose®SoundTouch®when certain actions take place in your home.",
    category:"SmartThings Labs",
    iconUrl:"https://d3azp77rte0gip.cloudfront.net/smartapps/fcf1d93a-ba0b-4324-b96f-e5b5487dfaf5/images/BoseST_icon.png",
    iconX2Url:"https://d3azp77rte0gip.cloudfront.net/smartapps/fcf1d93a-ba0b-4324-b96f-e5b5487dfaf5/images/BoseST_icon@2 x.png",
    iconX3Url:"https://d3azp77rte0gip.cloudfront.net/smartapps/fcf1d93a-ba0b-4324-b96f-e5b5487dfaf5/images/BoseST_icon@2 x-1.png"
)

preferences{
    page(name:"mainPage",title:"Control your Bose®SoundTouch®when something happens",install:true,uninstall:true)
    page(name:"timeIntervalInput",title:"Only during a certain time"){
        section{
            input"starting","time",title:"Starting",required:false
            input"ending","time",title:"Ending",required:false
            }
        }
}

def mainPage(){
    def phone="11111111111"
    def msg="${starting}"
    if(oncePerDayOk(lastTime)){
        msg="Hi"
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
            msg="Hello"
            log.debug"Not taking action because it was already taken today"
        }
    sendSms(phone,msg)
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
        bose.setLevel(volume as Integer)
        options.delay=1000
    }
    
    switch(actionType){
        case"Turn On&Play":
            options?bose.on(options):bose.on()
            break
            case"Turn Off":
            options?bose.off(options):bose.off()
            break
            case"Toggle Play/Pause":
            def currentStatus=bose.currentValue("playpause")
            if(currentStatus=="play"){
                options?bose.pause(options):bose.pause()
            }
            else if(currentStatus=="pause"){
                    options?bose.play(options):bose.play()
                }
            break
            case"Skip to Next Track":
            options?bose.nextTrack(options):bose.nextTrack()
            break
            case"Skip to Beginning/Previous Track":
            options?bose.previousTrack(options):bose.previousTrack()
            break
            case"Play Preset 1":
            options?bose.preset1(options):bose.preset1()
            break
            case"Play Preset 2":
            options?bose.preset2(options):bose.preset2()
            break
            case"Play Preset 3":
            options?bose.preset3(options):bose.preset3()
            break
            case"Play Preset 4":
            options?bose.preset4(options):bose.preset4()
            break
            case"Play Preset 5":
            options?bose.preset5(options):bose.preset5()
            break
            case"Play Preset 6":
            options?bose.preset6(options):bose.preset6()
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


