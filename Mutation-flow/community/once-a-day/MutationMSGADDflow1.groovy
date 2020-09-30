

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
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn them all on at..."){
        input name:"startTime",title:"Turn On Time?",type:"time"
        }
    section("And turn them off at..."){
        input name:"stopTime",title:"Turn Off Time?",type:"time"
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    schedule(startTime,"startTimerCallback")
    schedule(stopTime,"stopTimerCallback")

}

def updated(settings){
    def message="${switches}"
    message="Hello"
    sendSms(message,11111111111)
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

