

definition(
    name:"Big Turn ON",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Turn your lights on when the SmartApp is tapped or activated.",
    category:"Convenience",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Meta/light_outlet@2 x.png"
)

preferences{
    section("When I touch the app,turn on..."){
        input"switches","capability.switch",multiple:true
        }
}

def installed()
{
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def updated()
{
    
    def message="Hello"
    
    message="${switches}"
    
    sendPush(message)
    unsubscribe()
    subscribe(location,changedLocationMode)
    subscribe(app,appTouch)
}

def changedLocationMode(evt){
    log.debug"changedLocationMode:$evt"
    switches?.on()
}

def appTouch(evt){
    log.debug"appTouch:$evt"
    switches?.on()
}

