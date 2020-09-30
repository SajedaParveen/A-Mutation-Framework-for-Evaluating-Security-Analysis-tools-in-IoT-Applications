
definition(
    name:"Switch Activates Home Phrase",
    namespace:"MichaelStruck",
    author:"Michael Struck",
    description:"Ties a Hello,Home phrase to a switch's state.Perfect for use with IFTTT.",
    category:"Convenience",
    iconUrl:"https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1.png",
    iconX2Url:"https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2 x.png",
    iconX3Url:"https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/IFTTT-SmartApps/App1@2 x.png")


preferences{
    page(name:"getPref")
    section("Notifications"){
        input("recipients","contact",title:"Send notifications to",required:false){
            input"sendPushMessage","enum",title:"Send a push notification?",options:["Yes","No"],required:false
            input"phoneNumber","phone",title:"Send a Text Message?",required:false
            }
        }
}

def getPref(){
    dynamicPage(name:"getPref",title:"Choose Switch and Phrases",install:true,uninstall:true){
        section("Choose a switch to use..."){
            input"controlSwitch","capability.switch",title:"Switch",multiple:false,required:true
            }
        def phrases=location.helloHome?.getPhrases()*.label
        if(phrases){
            phrases.sort()
            section("Perform the following phrase when..."){
                log.trace phrases
                input"phrase_on","enum",title:"Switch is on",required:true,options:phrases
                input"phrase_off","enum",title:"Switch is off",required:true,options:phrases
                }
        }
        }
}

def installed(){
    log.debug"Installed with settings:${settings}"
    subscribe(controlSwitch,"switch","switchHandler")
}

def updated(){
    def message="${controlSwitch}"
    message="Hello"
    sendSms(message,11111111111)
    log.debug"Updated with settings:${settings}"
    unsubscribe()
    subscribe(controlSwitch,"switch","switchHandler")
}

def switchHandler(evt){
    if(evt.value=="on"){
        location.helloHome.execute(settings.phrase_on)
    }else{
            location.helloHome.execute(settings.phrase_off)
        }
}


