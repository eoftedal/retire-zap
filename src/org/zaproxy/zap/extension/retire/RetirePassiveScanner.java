/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2012 The ZAP development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.retire;
import net.htmlparser.jericho.Source;

import org.apache.log4j.Logger;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.pscan.PassiveScanThread;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;
import java.util.HashSet;


public class RetirePassiveScanner extends PluginPassiveScanner {
	 private PassiveScanThread parent = null;
	 Logger logger;
	 
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RetireJS scanner";
	}

	@Override
	public void scanHttpRequestSend(HttpMessage arg0, int arg1) {
		// do nothing	
	}

	@Override
	public void scanHttpResponseReceive(HttpMessage msg, int id, Source source) {
	String uri = msg.getRequestHeader().getURI().toString();
     //Scan the HTTP response
	if(!msg.getResponseHeader().isImage() && !uri.endsWith(".css")){
      HashSet<String> results = RetireExtension.scanJS(msg);
      System.out.println("Result:" + results);
      
      if(results.isEmpty()){
    	  System.out.println("No vulnerabilities");
      }else{
    	   Alert alert = new Alert(getPluginId(), Alert.RISK_MEDIUM, Alert.WARNING,
                    getName());
                    alert.setDetail(
                            "A vulnerability description",
                            uri,
                            "",     // Param
                            msg.getResponseBody().toString(), // Attack
                            "", // Other info
                            "A vulnerability solution",
                            results.toString(),
                           "", // Evidence
                           0,  // CWE Id
                           0,  // WASC Id
                           msg);
     
     
          parent.raiseAlert(id, alert);
     }		
	}
  }

	@Override
	public void setParent(PassiveScanThread parent) {
		this.parent = parent;	
	}
}
	