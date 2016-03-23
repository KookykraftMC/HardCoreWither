package thor12022.hardcorewither.config;

import net.minecraftforge.common.config.Configuration;
import thor12022.hardcorewither.HardcoreWither;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ConfigManager
{   
   private ArrayList<Object> configObjects = new ArrayList<Object>();
   private ArrayList<Class<?>> configClasses = new ArrayList<Class<?>>();
   private Configuration configuration;
   
   public ConfigManager(String modName)
   {
      File config = new File(System.getProperty("user.dir") + File.separatorChar + 
                             "CONFIG" + File.separatorChar + 
                             modName + ".cfg");
      configuration = new Configuration(config);
   }
   
   /**
    * Register a class instance for configuration
    * @param target the object that contains the configurable fields.
    *    Must use the @Configurable annotation
    */
   public void register(Object target)
   {
      if(target instanceof Class)
      {
         Configurable annotation = ((Class<?>)target).getAnnotation(Configurable.class);
         if(annotation != null)
         {
            try
            {
               Class.forName(((Class) target).getName());
               configClasses.add(((Class<?>)target));
               processConfigClass(((Class<?>)target));
               configuration.save();
            }
            catch(ClassNotFoundException e)
            {
               HardcoreWither.LOGGER.error(e);
            }
         }
      }
      else
      {
         Configurable annotation = target.getClass().getAnnotation(Configurable.class);
         if(annotation != null)
         {
            configObjects.add(target);
            processConfigObject(target);
            configuration.save();
         }
      }
   }

   public void syncConfig()
   {
      if(configuration != null)
      {
         for(Object configObject : configObjects)
         {
            processConfigObject(configObject);
         }
         for(Class<?> configClass : configClasses)
         {
            processConfigClass(configClass);
         }
         configuration.save();
         for(Object configClass : configObjects)
         {
            processNotificationClass(configClass);
         }
      }
   }
   
   public File getConfigFile()
   {
      return configuration.getConfigFile();
   }
   
   private void processConfigObject(Object target)
   {
      try
      {
         Configurable targetAnnotation = target.getClass().getAnnotation(Configurable.class);
         String sectionName = targetAnnotation.sectionName();
         if(sectionName.isEmpty())
         {
            sectionName = target.getClass().getSimpleName();
         }
         processClass(sectionName, target, target.getClass());
      }
      catch(Exception e)
      {
         HardcoreWither.LOGGER.warn(e);
         HardcoreWither.LOGGER.warn("Cannot find @Configurable Annotation on " + target.getClass().getName());
      }
   }
   
   private void processConfigClass(Class<?> target)
   {
      try
      {
         Configurable targetAnnotation = target.getAnnotation(Configurable.class);
         String sectionName = targetAnnotation.sectionName();
         if(sectionName.isEmpty())
         {
            sectionName = target.getSimpleName();
         }
         processClass(sectionName, null, target);
      }
      catch(Exception e)
      {
         HardcoreWither.LOGGER.warn(e);
         HardcoreWither.LOGGER.warn("Cannot find @Configurable Annotation on " + target.getName());
      }
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
            HardcoreWither.LOGGER.warn("Invalid Config callback method: " + target.getClass().getName() + "." + callbackName);
         }
      }
   }
   
   /**
    * Recurses over the class hierarchy of an object
    * @param sectionName name of the CONFIG section to write to
    * @param classObj object currently being processed 
    * @param currentClass the level in the classObj 's hierarchy being processed
    **/
   private void processClass(String sectionName, Object classObj, Class<?> currentClass)
   {
      if(currentClass == null || currentClass == Object.class)
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
               HardcoreWither.LOGGER.warn(excp);
               String extraError = classObj == null ? "Possibly nonstatic field used with static Class Registration" : "";
               HardcoreWither.LOGGER.warn("Problem getting configurable field \"" + currentClass.getName() + "." + field.getName() + "\"." + extraError); 
            }
         }
      }
   }  
}