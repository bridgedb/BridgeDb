# Package SQL

## Purpose
1. Provide SQL based Implementation of IDMapper ect.
   1. MySQl based version
   2. Virtuoso sql engine based version (under development)
2. Groups mappings into MappingSets each with a single source DataSource mapping to a single Target DataSource
   1. This to link mappings to void linksets and therefor the provinence in the void headers

Similar to RDB package but:
1. Designed for speed with no attempt to save space.
   1. No self join used
2. Store assume mappings are not symetric.
   1. Symetric mappings loaded in both directions
3. MappingSets have a predicate assigned.
4. MappingSets have a field to save if they are "transitive". 
   (badly named flag to identify linksets/mappingsets) created by using transitivity of two other mappingsets)
5. FUTURE WORK: Mapping sets to have extra information so they are sometimes used sometimes not.
   1. Example exact mappings vs near mappings
