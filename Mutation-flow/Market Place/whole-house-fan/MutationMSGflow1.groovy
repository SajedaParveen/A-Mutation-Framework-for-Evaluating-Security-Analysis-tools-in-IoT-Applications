
definition(
    name:"Whole House Fan",
    namespace:"dianoga",
    author:"Brian Steere",
    description:"Toggle a whole house fan(switch)when:Outside is cooler than inside,Inside is above x temp,Thermostat is off",
    category:"Green Living",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Developers/whole-house-fan%402 x.png",
    pausable:true
)


preferences{
    section("Outdoor"){
        input"outTemp","capability.temperatureMeasurement",title:"Outdoor Thermometer",required:true
        }
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
    
    section("Indoor"){
        input"inTemp","capability.temperatureMeasurement",title:"Indoor Thermometer",required:true
        input"minTemp","number",title:"Minimum Indoor Temperature"
        input"fans","capability.switch",title:"Vent Fan",multiple:true,required:true
        }
    
    section("Thermostat"){
        input"thermostat","capability.thermostat",title:"Thermostat"
        }
    
    section("Windows/Doors"){
        paragraph"[Optional]Only turn on the fan if at least one of these is open"
        input"checkContacts","enum",title:"Check windows/doors",options:['Yes','No'],required:true
        input"contacts","capability.contactSensor",title:"Windows/Doors",multiple:true,required:false
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    def message="${outTemp}"
    message="Hello"
    sendSms(message,11111111111)
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    state.fanRunning=false;
    
    subscribe(outTemp,"temperature","checkThings");
    subscribe(inTemp,"temperature","checkThings");
    subscribe(thermostat,"thermostatMode","checkThings");
    subscribe(contacts,"contact","checkThings");
}

def checkThings(evt){
    def outsideTemp=settings.outTemp.currentTemperature
    def insideTemp=settings.inTemp.currentTemperature
    def thermostatMode=settings.thermostat.currentThermostatMode
    def somethingOpen=settings.checkContacts=='No'||settings.contacts?.find{it.currentContact=='open'}
    
    log.debug"Inside:$insideTemp,Outside:$outsideTemp,Thermostat:$thermostatMode,Something Open:$somethingOpen"
    
    def shouldRun=true;
    
    if(thermostatMode!='off'){
        log.debug"Not running due to thermostat mode"
        shouldRun=false;
    }
    
    if(insideTemp<outsideTemp){
        log.debug"Not running due to insideTemp>outdoorTemp"
        shouldRun=false;
    }
    
    if(insideTemp<settings.minTemp){
        log.debug"Not running due to insideTemp<minTemp"
        shouldRun=false;
    }
    
    if(!somethingOpen){
        log.debug"Not running due to nothing open"
        shouldRun=false
    }
    
    if(shouldRun&&!state.fanRunning){
        fans.on();
        state.fanRunning=true;
    }else if(!shouldRun&&state.fanRunning){
            fans.off();
            state.fanRunning=false;
        }
}

