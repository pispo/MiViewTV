package com.movistar.miviewtv.stb;

import com.movistar.miviewtv.core.MovistarFile;

import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

public class BootConfig {
   private static final String LOG_TAG = BootConfig.class.getSimpleName();
  
   private static final String DEFAULT_OPCH_ADDR = "239.0.2.10";
   private static final int DEFAULT_POCH_PORT = 22222;
  
   private static final String CONFIG_RESOURCES_FILE = "0001_4000_cfg.resources.url";
   private static final String VAR_UTCTIME_FILE = "0007_0001_var.utctime";
  
   private static List<String> configFiles = new ArrayList<String> ();
  
   String opchAddress;
   int opchPort;
  
   private static BootConfig instance = null;
    
   static {
      configFiles.add (CONFIG_RESOURCES_FILE);
      configFiles.add (VAR_UTCTIME_FILE);
   }  
  
   public BootConfig() {
      this(DEFAULT_OPCH_ADDR, DEFAULT_POCH_PORT);
   }
  
   public static getInstance()
   {
      if (instance == null)
          instance = new BootConfig();
     
      return instance;
   }
  
   public void download () throws BootConfigException
   {
       Infocast infocastInfo = null;
     
       try {
           infocastInfo = new Infocast(this.opchAddress, this.opchPort);
       } catch (IOException e) {
           throw new BootConfigException("Failed to connect infocast opch", e);
       }
   }
  
   private MovistarFile readBootConfigFile(String configFileName) throws BootConfigException {
          
        try {
          
        } catch (IOException e) {
            throw new BootConfigException("Failed to connect file " + configFileName, e);
        }
   }  
  
   /**
    * Exception thrown for all bootconfig parsing errors. 
    */ 
   static public class BootConfigException extends RuntimeException { 
 
      private static final long serialVersionUID = 1L; 
 
      public BootConfigException(String msg, Throwable cause) { 
          super(msg, cause); 
      } 
   }
}
