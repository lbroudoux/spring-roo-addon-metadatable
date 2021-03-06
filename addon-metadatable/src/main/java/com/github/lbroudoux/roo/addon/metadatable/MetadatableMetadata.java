package com.github.lbroudoux.roo.addon.metadatable;

import static org.springframework.roo.model.JpaJavaType.ELEMENT_COLLECTION;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.roo.classpath.PhysicalTypeIdentifierNamingUtils;
import org.springframework.roo.classpath.PhysicalTypeMetadata;
import org.springframework.roo.classpath.details.FieldMetadata;
import org.springframework.roo.classpath.details.FieldMetadataBuilder;
import org.springframework.roo.classpath.details.MethodMetadata;
import org.springframework.roo.classpath.details.MethodMetadataBuilder;
import org.springframework.roo.classpath.details.annotations.AnnotatedJavaType;
import org.springframework.roo.classpath.details.annotations.AnnotationMetadataBuilder;
import org.springframework.roo.classpath.itd.AbstractItdTypeDetailsProvidingMetadataItem;
import org.springframework.roo.classpath.itd.InvocableMemberBodyBuilder;
import org.springframework.roo.metadata.MetadataIdentificationUtils;
import org.springframework.roo.model.DataType;
import org.springframework.roo.model.JavaSymbolName;
import org.springframework.roo.model.JavaType;
import org.springframework.roo.project.LogicalPath;

/**
 * This type produces metadata for a new ITD. It uses an {@link ItdTypeDetailsBuilder} provided by 
 * {@link AbstractItdTypeDetailsProvidingMetadataItem} to register a field in the ITD and a new method.
 * 
 * @since 1.1.0
 */
public class MetadatableMetadata extends AbstractItdTypeDetailsProvidingMetadataItem {

    // Constants
	private static final String PROVIDES_TYPE_STRING = MetadatableMetadata.class.getName();
	private static final String PROVIDES_TYPE = MetadataIdentificationUtils.create(PROVIDES_TYPE_STRING);

	private String entityName;
   private JavaType metadatasType = new JavaType(java.util.Map.class.getName(), 0, DataType.TYPE, null,
         Arrays.asList(JavaType.STRING, JavaType.STRING));
   
    public static final String getMetadataIdentiferType() {
        return PROVIDES_TYPE;
    }
    
    public static final String createIdentifier(JavaType javaType, LogicalPath path) {
        return PhysicalTypeIdentifierNamingUtils.createIdentifier(PROVIDES_TYPE_STRING, javaType, path);
    }

    public static final JavaType getJavaType(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getJavaType(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static final LogicalPath getPath(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.getPath(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }

    public static boolean isValid(String metadataIdentificationString) {
        return PhysicalTypeIdentifierNamingUtils.isValid(PROVIDES_TYPE_STRING, metadataIdentificationString);
    }
    
	public MetadatableMetadata(String identifier, JavaType aspectName, PhysicalTypeMetadata governorPhysicalTypeMetadata) {
		super(identifier, aspectName, governorPhysicalTypeMetadata);
		Validate.isTrue(isValid(identifier), "Metadata identification string '" + identifier + "' does not appear to be a valid");

		// Initialize entity name.
      entityName = getJavaType(identifier).getSimpleTypeName();
      
		// Add metadatas fields and methods.
		builder.addField(getMetadatasField());
		builder.addMethod(getMetadatasAccessor());
		builder.addMethod(getMetadataAddMethod());
		builder.addMethod(getMetadataKeyFinderMethod());
		builder.addMethod(getMetadataKeyValueFinderMethod());
		
		// Create a representation of the desired output ITD
		itdTypeDetails = builder.build();
	}
	
	/**
	 * Create metadata for a field definition. 
	 * @return a FieldMetadata object
	 */
	private FieldMetadata getMetadatasField() {
	   List<AnnotationMetadataBuilder> annotations = new ArrayList<AnnotationMetadataBuilder>();
      annotations.add(new AnnotationMetadataBuilder(ELEMENT_COLLECTION));
		
		// Using the FieldMetadataBuilder to create the field definition. 
		FieldMetadataBuilder fieldBuilder = new FieldMetadataBuilder(getId(), // Metadata ID provided by supertype
			Modifier.PRIVATE, 
			annotations, 
			new JavaSymbolName("metadatas"), // Field name
			metadatasType); // Field type
		
		fieldBuilder.setFieldInitializer("new java.util.HashMap<String, String>()");
		
		return fieldBuilder.build(); // Build and return a FieldMetadata instance
	}
	
	private MethodMetadata getMetadatasAccessor() {
		// Specify the desired method name
		JavaSymbolName methodName = new JavaSymbolName("getMetadatas");
		
		// Check if a method with the same signature already exists in the target type
		MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
		if (method != null) {
			// If it already exists, just return the method and omit its generation via the ITD
			return method;
		}
		
		// Define method parameter types (none in this case)
		List<AnnotatedJavaType> parameterTypes = new ArrayList<AnnotatedJavaType>();
		
		// Define method parameter names (none in this case)
		List<JavaSymbolName> parameterNames = new ArrayList<JavaSymbolName>();
		
		// Create the method body
		InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
		bodyBuilder.appendFormalLine("return this.metadatas;");
		
		// Use the MethodMetadataBuilder for easy creation of MethodMetadata
		MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
		      metadatasType, parameterTypes, parameterNames, bodyBuilder);
		
		return methodBuilder.build(); // Build and return a MethodMetadata instance
	}
	
	private MethodMetadata getMetadataAddMethod() {
	   // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("addMetadata");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         return method;
      }
      
      // Define method parameter types
      List<JavaType> parameterTypes = Arrays.asList(JavaType.STRING, JavaType.STRING);
      
      // Define method parameter names
      List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("key"), new JavaSymbolName("value"));
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("this.metadatas.put(key, value);");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC, methodName, 
            JavaType.VOID_PRIMITIVE, AnnotatedJavaType.convertFromJavaTypes(parameterTypes),
            parameterNames, bodyBuilder);
      
      return methodBuilder.build();
	}
	
	private MethodMetadata getMetadataKeyFinderMethod() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("findAll" + entityName + "sWithMetadata");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         // If it already exists, just return the method and omit its generation via the ITD
         return method;
      }
      
      // Define method parameter types
      List<JavaType> parameterTypes = Arrays.asList(JavaType.STRING);
      
      // Define method parameter names
      List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("key"));
      
      // Define return type.
      JavaType returnType = JavaType.listOf(destination);
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return entityManager().createQuery(\"SELECT o FROM " 
            + entityName + " o, IN (o.metadatas) key WHERE index(value)='\" + key + \"'\").getResultList();");
      
      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC | Modifier.STATIC, methodName, 
            returnType, AnnotatedJavaType.convertFromJavaTypes(parameterTypes), 
            parameterNames, bodyBuilder);
      
      return methodBuilder.build();
	}
	
	private MethodMetadata getMetadataKeyValueFinderMethod() {
      // Specify the desired method name
      JavaSymbolName methodName = new JavaSymbolName("findAll" + entityName + "sWithMetadataValue");
      
      // Check if a method with the same signature already exists in the target type
      MethodMetadata method = methodExists(methodName, new ArrayList<AnnotatedJavaType>());
      if (method != null) {
         // If it already exists, just return the method and omit its generation via the ITD
         return method;
      }
      
      // Define method parameter types
      List<JavaType> parameterTypes = Arrays.asList(JavaType.STRING, JavaType.STRING);
      
      // Define method parameter names
      List<JavaSymbolName> parameterNames = Arrays.asList(new JavaSymbolName("key"), new JavaSymbolName("value"));

      // Define return type.
      JavaType returnType = JavaType.listOf(destination);
      
      // Create the method body
      InvocableMemberBodyBuilder bodyBuilder = new InvocableMemberBodyBuilder();
      bodyBuilder.appendFormalLine("return entityManager().createQuery(\"SELECT o FROM " 
            + entityName + " o, IN (o.metadatas) value WHERE value='\" + value + \"'" 
            + " AND index(value)='\" + key + \"'\").getResultList();");

      // Use the MethodMetadataBuilder for easy creation of MethodMetadata
      MethodMetadataBuilder methodBuilder = new MethodMetadataBuilder(getId(), Modifier.PUBLIC | Modifier.STATIC, methodName, 
            returnType, AnnotatedJavaType.convertFromJavaTypes(parameterTypes), 
            parameterNames, bodyBuilder);
      
      return methodBuilder.build();
	}
	
	private MethodMetadata methodExists(JavaSymbolName methodName, List<AnnotatedJavaType> paramTypes) {
		// We have no access to method parameter information, so we scan by name alone and treat any match as authoritative
		// We do not scan the superclass, as the caller is expected to know we'll only scan the current class
		for (MethodMetadata method : governorTypeDetails.getDeclaredMethods()) {
			if (method.getMethodName().equals(methodName) && method.getParameterTypes().equals(paramTypes)) {
				// Found a method of the expected name; we won't check method parameters though
				return method;
			}
		}
		return null;
	}
	
	// Typically, no changes are required beyond this point
	
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("identifier", getId());
		tsb.append("valid", valid);
		tsb.append("aspectName", aspectName);
		tsb.append("destinationType", destination);
		tsb.append("governor", governorPhysicalTypeMetadata.getId());
		tsb.append("itdTypeDetails", itdTypeDetails);
		return tsb.toString();
	}
}
