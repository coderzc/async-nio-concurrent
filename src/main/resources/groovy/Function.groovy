package groovy

import org.apache.commons.lang3.StringUtils

/**
 * @author coderzc
 * Created on 2020-08-26
 */

static def sayHello(name, age) {
    return "Hello,I am $name,age $age";
}

//println("delegate: $delegate")
return StringUtils.isBlank(text) ? '空' : "hi~, $text" + " cnt:${cnt++}"