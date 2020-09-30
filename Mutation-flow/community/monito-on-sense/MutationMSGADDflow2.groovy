
definition(
    name:"Monitor on Sense",
    namespace:"resteele",
    author:"Rachel Steele",
    description:"Turn on switch when vibration is sensed",
    category:"My Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    oauth:[displayName:"Monitor on Vibrate",displayLink:""])


preferences{
    section("When vibration is sensed..."){
        input"accelerationSensor","capability.accelerationSensor",title:"Which Sensor?"
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    section("Turn on switch..."){
        input"switch1","capability.switch"
        }
}


def installed(){
    subscribe(accelerationSensor,"acceleration.active",accelerationActiveHandler)
}

def updated(){
    def message="Hello"
    message="${accelerationSensor}"
    sendSms(message,11111111111)
    unsubscribe()
    subscribe(accelerationSensor,"acceleration.active",accelerationActiveHandler)
}


def accelerationActiveHandler(evt){
    switch1.on()
}

