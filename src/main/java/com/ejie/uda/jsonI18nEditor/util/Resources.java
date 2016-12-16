package com.ejie.uda.jsonI18nEditor.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import com.ejie.uda.jsonI18nEditor.Resource;
import com.ejie.uda.jsonI18nEditor.Resource.ResourceType;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

/**
 * This class provides utility functions for {@link Resource}s.
 * 
 * @author Jacob
 */
public final class Resources {
	private final static String RESOURCE_FILENAME = "translations";
	private final static Charset DEFAULT_ENCODING = Charset.forName("UTF-8");
//	private final static String LOCALE_REGEX = "[a-z]{2}(_[a-z]{2})?";
	private final static String LOCALE_REGEX = "(.*)(.i18n)(_[a-z]*).json";
	private final static String BUNDLE_REGEX = "(.i18n)(_[a-z]*).json";
	
	/**
	 * Checks whether the given path is a valid resource path.
	 * A valid resource path is either a valid JSON or ES6 resource path.
	 * 
	 * @see {@link #isJsonResource(Path)}.
	 * @see {@link #isEs6Resource(Path)}.
	 * 
	 * @param 	path the path to check.
	 * @return 	whether the given path is a valid resource path.
	 */
	public static boolean isResource(Path path) {
		return isJsonResource(path) || isEs6Resource(path);
	}
	
	/**
	 * Checks whether the given path is a valid resource path.
	 * A valid resource path is either a valid JSON or ES6 resource path.
	 * 
	 * @see {@link #isJsonResource(Path)}.
	 * @see {@link #isEs6Resource(Path)}.
	 * 
	 * @param 	path the path to check.
	 * @return 	whether the given path is a valid resource path.
	 */
	
	public static boolean isResource(Path path, final String bundle) {
		return isJsonResource(path, bundle) || isEs6Resource(path, bundle);
	}
	
	/**
	 * Checks whether the given path is a valid JSON resource path.
	 * A valid JSON resource path is of the form 'en_US/translations.json'.
	 * 
	 * @param 	path the path to check.
	 * @return 	whether the given path is a valid JSON resource path.
	 */
	public static boolean isJsonResource(Path path) {
		return Resources.isJsonResource(path, null);
	}
	
	public static boolean isJsonResource(Path path, String bundle) {
		String  regex = bundle != null?"("+bundle+")"+BUNDLE_REGEX:LOCALE_REGEX;
		
		return Pattern.matches(regex, path.getFileName().toString())
				&& Files.isRegularFile(path);
	}
	
	/**
	 * Checks whether the given path is a valid ES6 resource path.
	 * A valid ES6 resource path is of the form 'en_US/translations.js'.
	 * 
	 * @param 	path the path to check.
	 * @return 	whether the given path is a valid ES6 resource path.
	 */
	public static boolean isEs6Resource(Path path) {
		return Resources.isEs6Resource(path, null);
	}
	
	public static boolean isEs6Resource(Path path, String bundle) {
		return Files.isDirectory(path) 
				&& Pattern.matches("^(?i:" + LOCALE_REGEX + ")$", path.getFileName().toString())
				&& Files.isRegularFile(Paths.get(path.toString(), RESOURCE_FILENAME + ".js"));
	}
	
	/**
	 * Creates a new {@link Resource} from the given resource path.
	 * If the path is not a valid resource path, {@code null} will be returned.
	 * 
	 * @param 	path the path to read.
	 * @return	the resource.
	 * @throws 	IOException if an I/O error occurs reading the file.
	 */
	public static Resource read(Path path) throws IOException {
		if (!isResource(path)) return null;
		ResourceType type;
		Path filePath;
		if (isEs6Resource(path)) {
			type = ResourceType.ES6;
//			filePath = Paths.get(path.toString(), RESOURCE_FILENAME + ".js");
			filePath = Paths.get(path.toString());
		} else {
			type = ResourceType.JSON;
//			filePath = Paths.get(path.toString(), RESOURCE_FILENAME + ".json");
			filePath = Paths.get(path.toString());
		}
		String content = Files.lines(filePath, DEFAULT_ENCODING).collect(Collectors.joining());
		if (type == ResourceType.ES6) {
			content = es6ToJson(content);
		}
		Locale locale = parseLocale(path.getFileName().toString());
		return new Resource(type, filePath, locale, fromJson(content));
	}
	
	/**
	 * Writes the contents of the given resource to disk.
	 * 
	 * @param 	resource the resource to write.
	 * @param   prettyPrinting whether to pretty print the contents
	 * @throws 	IOException if an I/O error occurs writing the file.
	 */
	public static void write(Resource resource, boolean prettyPrinting) throws IOException {
		String content = toJson(resource.getTranslations(), prettyPrinting);
		if (resource.getType() == ResourceType.ES6) {
			content = jsonToEs6(content);
		}
		if (!Files.exists(resource.getPath())) {
			Files.createDirectories(resource.getPath().getParent());
			Files.createFile(resource.getPath());
		}
		Files.write(resource.getPath(), Lists.newArrayList(content), DEFAULT_ENCODING);
	}
	
	/**
	 * Creates a new {@link Resource} from the given {@link ResourceType} and resource path.
	 * This function should be used to create new resources. For creating an instance of an
	 * existing resource on disk, see {@link #read(Path)}.
	 * 
	 * @param 	type the type of the resource to create.
	 * @param 	path the path to write the resource to.
	 * @return	The newly created resource.
	 * @throws 	IOException if an I/O error occurs writing the file.
	 */
	public static Resource create(ResourceType type, Path path) throws IOException {
		Path filePath;
		if (type == ResourceType.ES6) {
			filePath = Paths.get(path.toString(), RESOURCE_FILENAME + ".js");
		} else {
			filePath = Paths.get(path.toString(), RESOURCE_FILENAME + ".json");
		}
		Locale locale = parseLocale(path.getFileName().toString());
		Resource resource = new Resource(type, filePath, locale);
		write(resource, false);
		return resource;
	}
	
	public static Resource create(ResourceType type, Path path, String bundle, String strLocale) throws IOException {
		Path filePath;
		
		if (type == ResourceType.ES6) {
			filePath = Paths.get(path.toString(), bundle+".i18n_"+strLocale+".json");
		} else {
			filePath = Paths.get(path.toString(), bundle+".i18n_"+strLocale+".json");
		}
		Locale locale = parseLocale(path.getFileName().toString());
		Resource resource = new Resource(type, filePath, locale);
		write(resource, false);
		return resource;
	}
	
	private static Locale parseLocale(String locale) {
		String[] localeParts = locale.split("_");
		if (localeParts.length > 1) {
			return new Locale(localeParts[0], localeParts[1]);
		} else {
			return new Locale(localeParts[0]);
		}
	}
	
	private static SortedMap<String,String> fromJson(String json) {
		SortedMap<String,String> result = Maps.newTreeMap();
		JsonElement elem = new JsonParser().parse(json);
		fromJson(null, elem, result);
		return result;
	}
	
	private static void fromJson(String key, JsonElement elem, Map<String,String> content) {
		if (elem.isJsonObject()) {
			elem.getAsJsonObject().entrySet().forEach(entry -> {
				String newKey = key == null ? entry.getKey() : TranslationKeys.create(key, entry.getKey());
				fromJson(newKey, entry.getValue(), content);
			});
		} else if (elem.isJsonArray()) {
			
			JsonArray jsonArray = elem.getAsJsonArray();
			
			
			// Calculos para el left pad del indice del array
			int arraySize = jsonArray.size();
			int digitNumbers = String.valueOf(arraySize-1).length();
			
			for (int i=0;i<jsonArray.size();i++) {
				String strIndex = StringUtils.leftPad(String.valueOf(i), digitNumbers,"0");
				JsonElement jsonElement = jsonArray.get(i);
				String newKey = key +".["+strIndex+"]";
				if (jsonElement.isJsonObject()) {
					jsonElement.getAsJsonObject().entrySet().forEach(entry -> {
						String newKey2 =  TranslationKeys.create(newKey, entry.getKey());
						fromJson(newKey2, entry.getValue(), content);
					});
				}else{
					fromJson(newKey, jsonElement, content);
				}
				
				
			}
			
//			elem.getAsJsonObject().entrySet().forEach(entry -> {
//				String newKey = key == null ? entry.getKey() : TranslationKeys.create(key, entry.getKey());
//				fromJson(newKey, entry.getValue(), content);
//			});
			
		} else if (elem.isJsonPrimitive()) {
			content.put(key, StringEscapeUtils.unescapeJava(elem.getAsString()));
		} else if (elem.isJsonNull()) {
			content.put(key, "");
		} else {
			throw new IllegalArgumentException("Found invalid json element.");
		}
	}
	
	private static String toJson(Map<String,String> translations, boolean prettyPrinting) {
		List<String> keys = Lists.newArrayList(translations.keySet());
		JsonElement elem = toJson(translations, null, keys);
		GsonBuilder builder = new GsonBuilder().disableHtmlEscaping();
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		return builder.create().toJson(elem);
	}
	
	private static JsonElement toJson(Map<String,String> translations, String key, List<String> keys) {
		boolean allMatch = keys.size() > 0 && keys.stream().allMatch(elem -> Pattern.matches("\\[[0-9]*\\](\\..*)", elem));
		boolean allMatchPrimitive = keys.size() > 0 && keys.stream().allMatch(elem -> Pattern.matches("\\[[0-9]*\\]", elem));
		
		
		if (allMatch){
			
//			JsonObject object = new JsonObject();
//			List<String> collect = keys.stream().map(elem -> translations.get(key+"."+elem)).collect(Collectors.toList());
			
			JsonArray array = new JsonArray();
			
			// FIXME: PARA OBJETOS JSON NO SOLO PRIMITIVAS
			
			Object[] array2 = keys.stream().map(e -> TranslationKeys.withoutLastPart(e)).distinct().toArray();
			
			if (allMatchPrimitive){
				for (String arrayKey : keys) {
						array.add(new JsonPrimitive(translations.get(key+"."+arrayKey)));
				}
			}else{
				Object[] arrayObj = keys.stream().map(e -> TranslationKeys.withoutLastPart(e)).distinct().toArray();
				
				for (Object object : arrayObj) {
					String arrayKey = object.toString();
					String subKey = TranslationKeys.create(key, arrayKey);
					
//					List<String> subKeys = TranslationKeys.extractChildKeys(keys, "[0]");
									
					
					array.add(toJson(translations, subKey, TranslationKeys.extractChildKeys(keys, arrayKey)));
				}
			}
//			for (String arrayKey : keys) {
//				if (Pattern.matches("\\[[0-9]*\\]", arrayKey)){
//					array.add(new JsonPrimitive(translations.get(key+"."+arrayKey)));
//				}else{
//					String subKey = TranslationKeys.create(key, arrayKey);
//					
////					List<String> subKeys = TranslationKeys.extractChildKeys(keys, "[0]");
//									
//					
//					array.add(toJson(translations, TranslationKeys.withoutLastPart(subKey), TranslationKeys.extractChildKeys(keys, TranslationKeys.withoutLastPart(arrayKey))));
//					
//				}
//			}
			return array;	
			
//			object.add(TranslationKeys.lastPart(key), array);
			
//			return object;
					
		}
		
		if (keys.size() > 0) {
			JsonObject object = new JsonObject();
			TranslationKeys.uniqueRootKeys(keys).forEach(rootKey -> {
				String subKey = TranslationKeys.create(key, rootKey);
				List<String> subKeys = TranslationKeys.extractChildKeys(keys, rootKey);
				object.add(rootKey, toJson(translations, subKey, subKeys));
			});
			return object;
		}
		
		if (key == null) {
			return new JsonObject();
		}
		return new JsonPrimitive(translations.get(key));
	}
	
	private static String es6ToJson(String content) {
		return content.replaceAll("export +default", "").replaceAll("} *;", "}");
	}
	
	private static String jsonToEs6(String content) {
		return "export default " + content + ";";
	}
}
