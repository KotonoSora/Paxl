# Paxl UI (screens, VMs, adapters)
-keep class local.paxl.ui.** { *; }
-keep class local.paxl.engine.** { *; }
# Room (via core-ui) — keep here too in case UI references entities
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
