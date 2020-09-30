
definition(
    name:"CO2 Vent",
    namespace:"dianoga",
    author:"Brian Steere",
    description:"Turn on a switch when CO2 levels are too high",
    category:"Health&Wellness",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    pausable:true
)

preferences{
    section("CO2 Sensor"){
        input"sensor","capability.carbonDioxideMeasurement",title:"Sensor",required:true
        input"level","number",title:"CO2 Level",required:true
        }
    
    section("Ventilation Fan"){
        input"switches","capability.switch",title:"Switches",required:true,multiple:true
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    state.active=false;
    subscribe(sensor,"carbonDioxide",'handleLevel')
}

def handleLevel(evt){
    def co2=sensor.currentValue("carbonDioxide").toInteger()
    log.debug"CO2 Level:${co2}/${settings.level}Active:${state.active}"
    def msg="${sensor}"
    if(co2>=settings.level&&!state.active){
        msg="Hello"
        log.debug"Turning on"
        switches.each{it.on();}
        state.active=true;
    }
    else
    {
        msg="Hi"
    if(co2<settings.level&&state.active){
            log.debug"Turning off"
            state.active=false;
            switches.each{it.off();}
        }
    }
    sendPush(msg)
}

