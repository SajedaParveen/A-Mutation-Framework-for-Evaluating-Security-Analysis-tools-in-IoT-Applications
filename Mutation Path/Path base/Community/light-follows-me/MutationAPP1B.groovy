

definition(
    name:"Light Follows Me",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when motion is detected and then off again once the motion stops for a set period of time.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch@2 x.png"
)

preferences{
    section("Turn on when there's movement..."){
        input"motion1","capability.motionSensor",title:"Where?"
        }
    section("And off when there's been no movement for..."){
        input"minutes1","number",title:"Minutes?"
        }
    section("Turn on/off light(s)..."){
        input"switches","capability.switch",multiple:true
        }
}

def installed(){
    subscribe(motion1,"motion",motionHandler)
}

def updated(){
    unsubscribe()
    subscribe(motion1,"motion",motionHandler)
}

def motionHandler(evt){
    log.debug"$evt.name:$evt.value"
    def msg="Hello"
    msg="${motion1}"
    msg="Hello"
    if(evt.value=="active"){
        log.debug"turning on lights"
        switches.on()
    }else if(evt.value=="inactive"){
            runIn(minutes1*60,scheduleCheck,[overwrite:false])
        }
    sendPush(msg)
}

def scheduleCheck(){
    log.debug"schedule check"
    def motionState=motion1.currentState("motion")
    if(motionState.value=="inactive"){
        def elapsed=now()-motionState.rawDateCreated.time
        def threshold=1000*60*minutes1-1000
        if(elapsed>=threshold){
            log.debug"Motion has stayed inactive long enough since last check($elapsed ms):turning lights off"
            switches.off()
        }else{
                log.debug"Motion has not stayed inactive long enough since last check($elapsed ms):doing nothing"
            }
    }else{
            log.debug"Motion is active,do nothing and wait for inactive"
        }
}

