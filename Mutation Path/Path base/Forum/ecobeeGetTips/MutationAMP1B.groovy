
definition(
    name:"ecobeeGetTips",
    namespace:"yracine",
    author:"Yves Racine",
    description:"Get Energy Saving Tips from My Ecobee device",
    category:"My Apps",
    iconUrl:"https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url:"https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2 x.png"
)


preferences{
    
    page(name:"generalSetupPage",title:"General Setup",uninstall:true,nextPage:"displayTipsPage"){
        section("About"){
            paragraph"ecobeeGetTips,the smartapp that Get Comfort&Energy Saving Tips from My Ecobee device"
            paragraph"Version 1.1"
            paragraph"If you like this smartapp,please support the developer via PayPal and click on the Paypal link below"
            href url:"https://www.paypal.me/ecomatiqhomes",
            title:"Paypal donation..."
            paragraph"Copyright©2016 Yves Racine"
            href url:"http://github.com/yracine/device-type.myecobee",style:"embedded",required:false,title:"More information..."
            description:"http://github.com/yracine/device-type.myecobee/blob/master/README.md"
            }
        section("Get tips for this ecobee thermostat"){
            input"ecobee","device.myEcobeeDevice",title:"Ecobee Thermostat"
            }
        section("Level Of Tip"){
            input(name:"level",title:"Which level([1..4],4 is the highest,but not always applicable,ex.for multi-stage heating/cooling systems)?",type:"number",
            required:false,description:"optional")
            }
        section("Tip processing reset"){
            input(name:"resetTipFlag",title:"Do you want to re-start over and reset tips?",type:"bool")
            }
        }
    page(name:"displayTipsPage",content:"displayTipsPage",install:false,uninstall:true)
    page(name:"OtherOptions",title:"Other Options",install:true,uninstall:true){
        section([mobileOnly:true]){
            label title:"Assign a name for this SmartApp",required:false
            }
        }
}

def displayTipsPage(){
    if(resetTipFlag){
        log.debug("displayPageTips>about to call resetTips()")
        ecobee.resetTips()
    }
    log.debug("displayPageTips>about to call getTips()")
    ecobee.getTips(level)
    def tip1=ecobee.currentTip1Text
    def tip2=ecobee.currentTip2Text
    def tip3=ecobee.currentTip3Text
    def tip4=ecobee.currentTip4Text
    def tip5=ecobee.currentTip5Text
    def tip1Level=ecobee.currentTip1Level
    def tip2Level=ecobee.currentTip2Level
    def tip3Level=ecobee.currentTip3Level
    def tip4Level=ecobee.currentTip4Level
    def tip5Level=ecobee.currentTip5Level
    
    return dynamicPage(name:"displayTipsPage",title:"Display Current Tips",nextPage:"OtherOptions"){
        
        section("Tips"){
            
            
            def phone="11111111111"
            def msg="Hello"
            msg="${ecobee}"
            msg="Hi"if(tip1){
                paragraph image:"${getCustomImagePath()}/tip.jpg","**Tip1-Level$tip1Level**\n\n${tip1}\n"
            
            }else{
                    paragraph image:"${getCustomImagePath()}/tip.jpg","Based on the input data available,no tips may apply at this time for this level."+
                    "You can try with a different level of tips or later when the indoor/outdoor conditions have changed!"
                }
            sendSms(phone,msg)
            
            if(tip2){
                paragraph image:"${getCustomImagePath()}tip.jpg","**Tip2-Level$tip2Level**\n\n${tip2}\n"
            }
            if(tip3){
                paragraph image:"${getCustomImagePath()}tip.jpg","**Tip3-Level$tip3Level**\n\n${tip3}\n"
            }
            if(tip4){
                paragraph image:"${getCustomImagePath()}tip.jpg","**Tip4-Level$tip4Level**\n\n${tip4}\n"
            }
            if(tip5){
                paragraph image:"${getCustomImagePath()}tip.jpg","**Tip5-Level$tip5Level**\n\n${tip5}\n"
            }
            }
        }

}
def installed(){
    
    initialize()
}

def updated(){
    log.debug"Updated with settings:${settings}"
    
    unsubscribe()
    initialize()
}

def initialize(){

}


def getCustomImagePath(){
    return"http://raw.githubusercontent.com/yracine/device-type.myecobee/master/icons/"
}

