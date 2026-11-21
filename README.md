# Scena
Multi-Platform modding framework for Minecraft 1.18 and above

## Current state
**Stable**
But documentation and testing framework are still missing, basically a direct extraction from C&B and updated to 1.19.x.

### Features
* Modding framework
* Complete handling of the loading of your mod
* Complete freedom to create your own mod
* Jar-In-Jar capable, no external dependencies in your build script of on your mod.
* Completely modular, no need to know how to build a mod
* Platform transparent, no need to know what platform you are running on
* Handling of all the basic subsystem of Minecraft
* Custom network
* Custom configs
* Custom registries

### Experimental 26.2 terrain sharding

`-Dscena.experimentalTerrainSharding=true` enables the sharded section upload path. Default off;
Sodium owns terrain rendering when present, so the mixins stay disabled and a warning is logged.

A section layer's compiled vertex data is split when it exceeds the upload budget
`min(32 MiB, staging capacity, per-layer heap capacity)`, and the CPU builder rotates its
`ByteBufferBuilder` at the same 32 MiB vertex-byte limit, so a compile yields several meshes per layer
instead of one oversized buffer that the staging buffer cannot copy.

* `TerrainShardPlan` splits each source buffer into shard slices along the layer stride.
* `TerrainLayerShards` stages slices into the layer's `UberGpuBuffer`, tracks progress per slice
  and publishes one `SectionMesh` key per shard.
* `TerrainMeshState` keeps the shard set per section generation; a new upload or a released section
  drops the old shards, and a closed dispatcher cancels the pending tasks.
* The render pass draws the parent slice first, then every published shard slice from its
  vertex-buffer offset; a published shard without a GPU allocation is an error, not a skipped draw.

`ChunkSectionLayer.TRANSLUCENT` is never sharded, index buffers stay with the parent, and the
baked-model emission path is unchanged: sharding only moves bytes, not geometry.

### To do:
* Documentation
* Testing
* Integration with other mods
* Testing framework