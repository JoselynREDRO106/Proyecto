# Backups de SmartCampus UTA

Los backups se guardan en la carpeta local `backups/`, que esta ignorada por Git para evitar subir datos sensibles.

## Backup completo

```powershell
.\scripts\backup-all.ps1
```

## Backup solo de base de datos

```powershell
.\scripts\backup-db.ps1
```

Requiere tener `pg_dump` instalado y disponible en el `PATH`.

## Backup solo del codigo

```powershell
.\scripts\backup-code.ps1
```

## Restaurar base de datos

```powershell
.\scripts\restore-db.ps1 -BackupFile .\backups\smartcampus_db_YYYYMMDD_HHMMSS.dump
```

Antes de ejecutar los scripts, el archivo `.env` debe tener las variables:

```env
SUPABASE_DB_HOST=aws-1-us-east-2.pooler.supabase.com
SUPABASE_DB_PORT=5432
SUPABASE_DB_NAME=postgres
SUPABASE_DB_USER=postgres.xxxxxxxxxxxxxxxxxxxx
SUPABASE_DB_PASSWORD=TU_PASSWORD_REAL
```
