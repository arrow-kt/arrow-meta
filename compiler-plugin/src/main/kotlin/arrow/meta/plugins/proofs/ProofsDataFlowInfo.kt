package arrow.meta.plugins.proofs

import arrow.meta.log.Log
import arrow.meta.log.invoke
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowInfo
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.Nullability
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.util.javaslang.ImmutableMap
import org.jetbrains.kotlin.util.javaslang.ImmutableSet

class ProofsDataFlowInfo : DataFlowInfo by DataFlowInfo.EMPTY {
  override fun establishSubtyping(value: DataFlowValue, type: KotlinType, languageVersionSettings: LanguageVersionSettings): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.establishSubtyping $value $type, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.establishSubtyping(value, type, languageVersionSettings)
    }

  override val completeNullabilityInfo: ImmutableMap<DataFlowValue, Nullability>
    get() =
      Log.Verbose({ "ProofsDataFlowInfo.completeNullabilityInfo" }) {
        DataFlowInfo.EMPTY.completeNullabilityInfo
      }

  override val completeTypeInfo: ImmutableMap<DataFlowValue, ImmutableSet<KotlinType>>
    get() =
      Log.Verbose({ "ProofsDataFlowInfo.completeTypeInfo" }) {
        DataFlowInfo.EMPTY.completeTypeInfo
      }

  override fun and(other: DataFlowInfo): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.and" }) {
      DataFlowInfo.EMPTY.and(other)
    }

  override fun assign(a: DataFlowValue, b: DataFlowValue, languageVersionSettings: LanguageVersionSettings): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.assign $a, $b, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.assign(a, b, languageVersionSettings)
    }

  override fun clearValueInfo(value: DataFlowValue, languageVersionSettings: LanguageVersionSettings): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.clearValueInfo $value, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.clearValueInfo(value, languageVersionSettings)
    }

  override fun disequate(a: DataFlowValue, b: DataFlowValue, languageVersionSettings: LanguageVersionSettings): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.disequate $a, $b, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.disequate(a, b, languageVersionSettings)
    }

  override fun equate(a: DataFlowValue, b: DataFlowValue, identityEquals: Boolean, languageVersionSettings: LanguageVersionSettings): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.equate $a, $b, $identityEquals, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.equate(a, b, identityEquals, languageVersionSettings)
    }

  override fun getCollectedNullability(key: DataFlowValue): Nullability =
    Log.Verbose({ "ProofsDataFlowInfo.getCollectedNullability $key" }) {
      DataFlowInfo.EMPTY.getCollectedNullability(key)
    }

  override fun getCollectedTypes(key: DataFlowValue, languageVersionSettings: LanguageVersionSettings): Set<KotlinType> =
    Log.Verbose({ "ProofsDataFlowInfo.getCollectedTypes $key, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.getCollectedTypes(key, languageVersionSettings)
    }

  override fun getStableNullability(key: DataFlowValue): Nullability =
    Log.Verbose({ "ProofsDataFlowInfo.getStableNullability $key" }) {
      DataFlowInfo.EMPTY.getStableNullability(key)
    }

  override fun getStableTypes(key: DataFlowValue, languageVersionSettings: LanguageVersionSettings): Set<KotlinType> =
    Log.Verbose({ "ProofsDataFlowInfo.getStableTypes $key, $languageVersionSettings" }) {
      DataFlowInfo.EMPTY.getStableTypes(key, languageVersionSettings)
    }

  override fun or(other: DataFlowInfo): DataFlowInfo =
    Log.Verbose({ "ProofsDataFlowInfo.or $other" }) {
      DataFlowInfo.EMPTY.or(other)
    }
}