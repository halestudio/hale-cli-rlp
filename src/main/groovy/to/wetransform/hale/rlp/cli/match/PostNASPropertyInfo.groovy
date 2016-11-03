package to.wetransform.hale.rlp.cli.match;

import java.util.List

import eu.esdihumboldt.hale.common.align.groovy.accessor.EntityAccessor
import eu.esdihumboldt.hale.common.align.groovy.accessor.PathElement;
import eu.esdihumboldt.hale.common.align.groovy.accessor.internal.EntityAccessorUtil;
import eu.esdihumboldt.hale.common.align.model.EntityDefinition
import eu.esdihumboldt.util.groovy.paths.Path;
import groovy.transform.CompileStatic
import groovy.transform.Immutable
import groovy.transform.ToString

@CompileStatic
@Immutable
@ToString(includePackage = false)
class PostNASPropertyInfo {

  String baseProperty
  List<String> path
  String typeCategory
  String typeName
  String cardinality

  static PostNASPropertyInfo fromDescription(String description) {
    String baseProperty
    List<String> path
    String typeCategory
    String typeName
    String cardinality

    if (description) {
      def parts = description.split(/\s/)
      if (parts.length >= 1) {
        baseProperty = parts[0]
      }
      if (parts.length >= 2) {
        String propertyString = parts[1]
        if (propertyString) {
          path = propertyString.split(/\|/) as List
        }
      }
      if (parts.length >= 3) {
        typeCategory = parts[2] ?: null
      }
      if (parts.length >= 4) {
        typeName = parts[3] ?: null
      }
      if (parts.length >= 5) {
        cardinality = parts[4] ?: null
      }
    }

    new PostNASPropertyInfo(baseProperty: baseProperty, path: path,
      typeCategory: typeCategory, typeName: typeName, cardinality: cardinality)
  }

  EntityDefinition findEntity(EntityDefinition typeEntity) {
    EntityAccessor accessor = new EntityAccessor(typeEntity)

    if (!baseProperty) {
      return null
    }

    accessor = accessor.findChildren(baseProperty)

    if (path) {
      path.each {
        accessor = accessor.findChildren(it)
      }
    }

    try {
      accessor.toEntityDefinition()
    } catch (IllegalStateException e) {
      // multiple results found
      def options = accessor.all().collect {
        EntityAccessorUtil.createEntity((Path<PathElement>)it)
      }

      def candidates = options.findAll {
        // prefer properties with AdV namespace
        def ns = it.definition.name.namespaceURI
        ns && ns.toLowerCase().contains('adv')
      }

      if (candidates.size() > 1) {
        def names = candidates.collect { it.definition.name }
        println "Multiple candidates found for match $names"
      }

      if (candidates.empty) {
        println "No candidates chosen for property path with multiple results"
        null
      }
      else {
        candidates[0]
      }
    }
  }

}
