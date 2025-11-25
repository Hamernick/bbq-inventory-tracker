package com.bbqreset.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_0_1 = object : Migration(0, 1) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `locations` (
                `id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `tz` TEXT NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `items` (
                `id` INTEGER NOT NULL,
                `clover_item_id` TEXT,
                `name` TEXT NOT NULL,
                `sku` TEXT,
                `location_id` INTEGER NOT NULL,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_items_clover_item_id`
            ON `items`(`clover_item_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_items_location_id`
            ON `items`(`location_id`)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `templates` (
                `id` INTEGER NOT NULL,
                `name` TEXT NOT NULL,
                `location_id` INTEGER NOT NULL,
                `holiday_code` TEXT,
                PRIMARY KEY(`id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_templates_location_id`
            ON `templates`(`location_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_templates_holiday_code`
            ON `templates`(`holiday_code`)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `template_items` (
                `template_id` INTEGER NOT NULL,
                `item_id` INTEGER NOT NULL,
                `start_qty` INTEGER NOT NULL,
                PRIMARY KEY(`template_id`, `item_id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_template_items_item_id`
            ON `template_items`(`item_id`)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `counters` (
                `date` TEXT NOT NULL,
                `item_id` INTEGER NOT NULL,
                `location_id` INTEGER NOT NULL,
                `start_qty` INTEGER NOT NULL,
                `sold_qty` INTEGER NOT NULL,
                `manual_adj` INTEGER NOT NULL,
                `closed_on` INTEGER,
                PRIMARY KEY(`date`, `item_id`, `location_id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_counters_date_item_id_location_id`
            ON `counters`(`date`, `item_id`, `location_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_counters_item_id`
            ON `counters`(`item_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_counters_location_id`
            ON `counters`(`location_id`)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `logs` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `ts` INTEGER NOT NULL,
                `actor` TEXT NOT NULL,
                `action` TEXT NOT NULL,
                `meta_json` TEXT NOT NULL
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_logs_ts`
            ON `logs`(`ts`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_logs_actor`
            ON `logs`(`actor`)
            """.trimIndent()
        )

        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `jobs` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `kind` TEXT NOT NULL,
                `scheduled_for` INTEGER NOT NULL,
                `status` TEXT NOT NULL,
                `last_error` TEXT,
                `dedupe_key` TEXT
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_jobs_kind`
            ON `jobs`(`kind`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_jobs_scheduled_for`
            ON `jobs`(`scheduled_for`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE UNIQUE INDEX IF NOT EXISTS `index_jobs_dedupe_key`
            ON `jobs`(`dedupe_key`)
            """.trimIndent()
        )
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `week_plans` (
                `week_start` TEXT NOT NULL,
                `item_id` INTEGER NOT NULL,
                `location_id` INTEGER NOT NULL,
                `quantity` INTEGER NOT NULL,
                PRIMARY KEY(`week_start`, `item_id`, `location_id`)
            )
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_week_plans_week_start_location_id`
            ON `week_plans`(`week_start`, `location_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_week_plans_item_id`
            ON `week_plans`(`item_id`)
            """.trimIndent()
        )
        database.execSQL(
            """
            CREATE INDEX IF NOT EXISTS `index_week_plans_location_id`
            ON `week_plans`(`location_id`)
            """.trimIndent()
        )
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_default INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_mon INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_tue INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_wed INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_thu INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_fri INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_sat INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE week_plans ADD COLUMN quantity_sun INTEGER NOT NULL DEFAULT 0")
        database.execSQL("UPDATE week_plans SET quantity_default = quantity")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE items ADD COLUMN unit_type TEXT NOT NULL DEFAULT 'count'")
    }
}

val AppDatabaseMigrations: Array<Migration> = arrayOf(
    MIGRATION_0_1,
    MIGRATION_1_2,
    MIGRATION_2_3,
    MIGRATION_3_4
)
