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

### Experimental 26.2 native terrain output

`-Dscena.experimentalNativeTerrainGeometry=true` enables the optional geometry compiler hook;
`-Dscena.experimentalTerrainSharding=true` independently enables native terrain sharding.
Both default off. Sodium remains unsupported.

`ITerrainGeometryContext.prepareNativeGeometry(TerrainGeometry)` accepts a complete,
immutable block of producer-resolved rectangles, without requiring a baked rectangle model:

* `TerrainGeometry(List<TerrainGeometryPart> parts)` preserves source part order.
* `TerrainGeometryPart(TriState ambientOcclusion, @Nullable BlockState appearance,
  int materialFlags, List<TerrainQuad> quads)` preserves part grouping and AO policy.
* `TerrainQuad(TerrainVertex v0, v1, v2, v3, Direction face,
  @Nullable Direction cullFace, BakedQuad.MaterialInfo material, int tintIndex)` preserves
  source vertex order, winding, exact resolved UVs, native material and cull group.
* `TerrainVertex(float x, float y, float z, float u, float v)` uses block-local positions.

Lists are defensively copied. The separate quad `tintIndex` must be explicitly mapped to
the **original compile state's color provider**, not copied blindly from an independently
indexed material table; the source material's tint index is not reinterpreted.
Preparation validates the **whole** block without writing and returns `null` when unsupported,
or a compile-local, one-shot `Runnable`. Return that action from the geometry model's
preflight and run it during emission. Never fall back after starting the action: failures
propagate, even after partial output. No side-effecting eligibility probes are needed.

The initial contract supports finite, axis-aligned rectangles inside the block with
outward winding, SOLID/CUTOUT layers, and only the standard animation material flag.
Unsupported flags, malformed rectangles and mixed/translucent blocks retain the original
model path so native translucent sorting is unchanged.

NeoForge creates short-lived BakedQuads **only as inputs to the active native lighter**.
The live compiler renderer owns enhanced/vanilla lighting, first-part/per-part/quad AO,
face culling, block offsets and tint. The writer copies lit colors, emission-adjusted
lightmaps, positions and UVs into section-owned builders acquired anew per quad.
Fabric instead emits the resolved vertices directly through the compiler's existing
FRAPI QuadEmitter and stock Indigo alternate renderer, which owns its distinct AO/flat
lighting, tint, culling, offsets and native output. Its original per-quad builder acquisition
is retained. Both paths therefore compose with optional quad-aligned Scena sharding.
Fabric additionally requires block-atlas sprites and valid item render types.
This is not allocation-free emission, a performance claim, or a guarantee of equivalent
lighting for different tessellations.

The older NeoForge-only `prepareNativeModel(BlockStateModel)` snapshots already-baked
models under the same one-shot lifecycle. Fabric returns `null` for this older API.
These snapshots retain `ExtendedBlockStateModelPart` (including nullable appearance state and
its AO policy) only when the source has that contract. Parts with any other interface,
including interfaces inherited from superclasses or loader mixins, reject native preparation.
Native preparation also rejects known alternative renderer/shader mods and foreign
Mixin-merged hooks on the compiler, block renderer or vertex-writing classes. This
conservative gate is not a guarantee of compatibility with arbitrary bytecode transformers.
The reviewed C&B `SectionCompilerMixin` timing callbacks (`onCompileStart`/`onCompileEnd`)
are allowed only on `SectionCompiler`, with their original callback signatures; this
does not permit other C&B or third-party hooks.

Fabric native geometry is restricted to stock Indigo; Sodium, Iris, Canvas, Indium,
replacement renderers and unreviewed renderer hooks fall back. General `sink()`/`lighter()`
and sprite-plus-bounds `axisAlignedQuad` emission remain unavailable: those inputs cannot
describe arbitrary source UV orientation. No shader material-identity, translucency,
or performance parity is claimed.

### To do:
* Documentation
* Testing
* Integration with other mods
* Testing framework