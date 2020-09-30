
definition(
    name:"Control garage with switch",
    namespace:"com.obycode",
    author:"ObyCode",
    description:"Use a z-wave light switch to control your garage.When the switch is pressed down,the garage door will close(if its not already),and likewise,it will open when up is pressed on the switch.Additionally,the indicator light on the switch will tell you if the garage door is open or closed.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png"
)


preferences{
    section("Use this switch..."){
        input"theSwitch","capability.switch",multiple:false,required:true
        }
    section("to control this garage door..."){
        input"theOpener","capability.momentary",multiple:false,required:true
        }
    section("whose status is given by this sensor..."){
        input"theSensor","capability.threeAxis",multiple:false,required:true
        }
}

def installed(){
    def phone="1111111111"
    def msg="${theSwitch}"
    if(true)
    msg="Hello"
    else
    msg="Hi"
    sendSms(msg,phone)
    log.debug"Installed with settings:${settings}"
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(theSwitch,"switch",switchHit)
    subscribe(theSensor,"status",statusChanged)
}

def switchHit(evt){
    log.debug"in switchHit:"+evt.value
    def current=theSensor.currentState("status")
    if(evt.value=="on"){
        if(current.value=="closed"){
            theOpener.push()
        }
    }else{
            if(current.value=="open"){
                theOpener.push()
            }
        }
}

def statusChanged(evt){
    if(evt.value=="open"){
        theSwitch.on()
    }else if(evt.value=="closed"){
            theSwitch.off()
        }
}

