package com.cbmm.shipsimulator.data.local.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Migração da versão 1 para a versão 2 do banco de dados.
 * Adiciona a tabela ship_routes para armazenar o histórico de rotas dos navios.
 */
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Cria a tabela ship_routes
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `ship_routes` (
                `id` TEXT NOT NULL, 
                `shipId` TEXT NOT NULL, 
                `latitude` REAL NOT NULL, 
                `longitude` REAL NOT NULL, 
                `timestamp` INTEGER NOT NULL, 
                `speed` REAL NOT NULL, 
                `heading` REAL NOT NULL, 
                PRIMARY KEY(`id`), 
                FOREIGN KEY(`shipId`) REFERENCES `ships`(`id`) ON DELETE CASCADE 
            )
        """.trimIndent())
        
        // Cria um índice na coluna shipId para melhorar o desempenho das consultas
        database.execSQL("CREATE INDEX IF NOT EXISTS `index_ship_routes_shipId` ON `ship_routes` (`shipId`)")
    }
}
