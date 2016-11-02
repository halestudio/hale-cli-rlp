package to.wetransform.hale.rlp.cli.match

import javax.xml.namespace.QName;

import eu.esdihumboldt.hale.common.align.model.Alignment
import eu.esdihumboldt.hale.common.align.model.MutableAlignment
import eu.esdihumboldt.hale.common.align.model.impl.DefaultAlignment;
import eu.esdihumboldt.hale.common.schema.model.TypeDefinition
import eu.esdihumboldt.hale.common.schema.model.TypeIndex
import groovy.transform.CompileStatic

/**
 * Schema matcher for PostNAS that uses information available in target schema
 * comments to determine the respective element in the reference schema.
 *
 * @author Simon Templer
 */
@CompileStatic
class PostNASSchemaMatcher implements SchemaMatcher {

  @Override
  public Alignment generateSchemaMatching(TypeIndex refSchema, TypeIndex targetSchema) {
    /*
     * First collect type names of reference types matching target types
     */

    // reference types to target types
    Map<QName, QName> typeMap = [:]
    // type infos for each target type
    Map<QName, Map> targetTypeInfo = [:]
    // candidate names for reference types to target types
    Map<String, QName> targetTypeCandidates = [:]

    def targetTypes = (Collection<TypeDefinition>) targetSchema.getMappingRelevantTypes()
    targetTypes.each {
      // try to determine reference name (and other information) from description
      def typeInfo = parseTypeInfo(it.description)

      def candidateName
      if (typeInfo) {
        //XXX DEBUG
//        println "Type $it.name"
//        println typeInfo

        targetTypeInfo[it.name] = typeInfo

        if (typeInfo.FeatureType) {
          candidateName = typeInfo.FeatureType
        }
      }
      else {
        //TODO do default matching? - right now rely on type info
        // candidateName = it.name.localPart.toLowerCase()
      }

      if (candidateName) {
        targetTypeCandidates[candidateName] = it.name
      }
    }

    // iterate through reference types and collect targets

    def refTypes = (Collection<TypeDefinition>) refSchema.getMappingRelevantTypes()
    refTypes.each {
      def localName = it.displayName

      QName candidate = targetTypeCandidates[localName]
      if (candidate) {
        typeMap[it.name] = candidate
      }
    }

    MutableAlignment alignment = new DefaultAlignment()

    typeMap.each { QName ref, QName target ->
      println "Type match $ref.localPart -> $target.localPart"

      relateTypes(alignment, refSchema.getType(ref), targetSchema.getType(target))
    }

    alignment
  }

  private Map<String, String> parseTypeInfo(String typeDescription) {
    if (!typeDescription) {
      return [:]
    }

    def sections = typeDescription.split(/,/)

    sections.collectEntries { String section ->
      def property = section.split(/:/)
      if (property && property.length > 1) {
        // property found
        def propertyName = property[0].trim()
        def propertyValue = property[1..-1].join('').trim()
        // remove quotes
        if (propertyValue.length() >= 2) {
          if (propertyValue[0] == '"') {
            propertyValue = propertyValue[1..-1]
          }
          if (propertyValue[-1] == '"') {
            propertyValue = propertyValue[0..-2]
          }
        }

        [(propertyName): propertyValue]
      }
      else {
        [:]
      }
    }
  }

  private void relateTypes(MutableAlignment alignment, TypeDefinition ref, TypeDefinition target) {
    //TODO
  }

}