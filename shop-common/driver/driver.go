package driver

import (
	"context"
	"database/sql"
	"os"
	"path/filepath"
	"time"

	_ "github.com/jackc/pgconn"
	_ "github.com/jackc/pgx/v4"
	_ "github.com/jackc/pgx/v4/stdlib"
)

type DB struct {
	SQL *sql.DB
}

type DBConfig struct {
	MaxOpenDBConn int
	MaxIdleDBConn int
	MaxDBLifetime time.Duration
}

func ConnectSQL(dsn string, withMigrate bool, dbConfig *DBConfig) (*DB, error) {
	db, err := sql.Open("pgx", dsn)
	if err != nil {
		return nil, err
	}

	db.SetMaxOpenConns(dbConfig.MaxOpenDBConn)
	db.SetMaxIdleConns(dbConfig.MaxIdleDBConn)
	db.SetConnMaxLifetime(dbConfig.MaxDBLifetime)

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	if err := db.PingContext(ctx); err != nil {
		return nil, err
	}

	pwd, err := os.Getwd()
	if err != nil {
		return nil, err
	}
	if withMigrate {
		migrationFilePath := filepath.Join(pwd, "db", "migrations")
		if err := Migration(migrationFilePath, db); err != nil {
			return nil, err
		}
	}

	return &DB{db}, nil
}
