package thor12022.hardcorewither.config;

/*
 * Creation and usage of the config file.
 */

import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConfigManager
{

   private static ConfigManager instance = new ConfigManager();
   
   private ArrayList<Object> configClasses = new ArrayList<Object>();
   private Configuration configuration;
   
   public void init(File config)
   {
      configuration = new Configuration(config);
      syncConfig();
   }
   
   public static ConfigManager getInstance()
   {
      return instance;
   }
   
   public void register(Object target)
   {
      Configurable annotation = target.getClass().getAnnotation(Configurable.class);
      if(annotation != null)
      {
         configClasses.add(target);
      }
   }

   public void syncConfig()
   {
      if(configuration != null)
      {
         for(Object configClass : configClasses)
         {
            processConfigClass(configClass);
         }
         configuration.save();
         for(Object configClass : configClasses)
         {
            processNotificationClass(configClass);
         }
      }
   }
   
   private void processConfigClass(Object target)
   {
      Configurable targetAnnotation = target.getClass().getAnnotation(Configurable.class);
      String sectionName = targetAnnotation.sectionName();
      if(sectionName.isEmpty())
      {
         sectionName = target.getClass().getSimpleName();
      }
      processClass(sectionName, target, target.getClass());
   }
   
   private static void processNotificationClass(Object target)
   {
      Configurable targetAnnotation = target.getClass().getAnnotation(Configurable.class);
      
      String callbackName = targetAnnotation.syncNotification();
      if(!callbackName.isEmpty())
      {
            try
         {
            Method method = target.getClass().getDeclaredMethod(callbackName);
            method.invoke(target);
         }
         catch(Exception excp)
         {
            //! @todo error reporting
         }
      }
   }
   
   /**
    * Recurses over the class hierarchy of an object
    * @param sectionName name of the config section to write to
    * @param classObj object currently being processed 
    * @param currentClass the level in the classObj 's hierarchy being processed
    **/
   private void processClass(String sectionName, Object classObj, Class<?> currentClass)
   {
      if(currentClass == Object.class)
      {
         return;
      }
      processClass(sectionName, classObj, currentClass.getSuperclass());
      for(Field field : currentClass.getDeclaredFields())
      {
         Config configAnnotation = field.getAnnotation(Config.class);
         if(configAnnotation != null)
         {
            String fieldName = configAnnotation.fieldName();
            if(fieldName.isEmpty())
            {
               fieldName = field.getName();
            }
            try
            {
               boolean isAccessible = field.isAccessible();
               field.setAccessible(true);
               if(field.getType() == boolean.class)
               {
                  field.set(classObj, configuration.getBoolean(fieldName, sectionName, field.getBoolean(classObj), configAnnotation.comment()));
               }
               else if(field.getType() == float.class)
               {
                  field.set(classObj, configuration.getFloat(fieldName, sectionName, field.getFloat(classObj), configAnnotation.minFloat(), configAnnotation.maxFloat(), configAnnotation.comment()));
               }
               else if(field.getType() == int.class)
               {
                  field.set(classObj, configuration.getInt(fieldName, sectionName, field.getInt(classObj), configAnnotation.minInt(), configAnnotation.maxInt(), configAnnotation.comment()));
               }
               else if(field.getType() == String.class)
               {
                  field.set(classObj, configuration.getString(fieldName, sectionName, (String)field.get(classObj), configAnnotation.comment()));
               }
               else
               {
                  processClass(sectionName, field.get(classObj), field.getType());
               }
               field.setAccessible(isAccessible);
            }
            catch(IllegalAccessException excp)
            {
               //! @todo Error Reporting
            }
         }
      }
   }  
}
