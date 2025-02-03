# base de datos de BD
## dependencias
debemos tener docker y docker compose instalado y docker levantado para poder correrlo

```bash
sudo pacman -S docker
sudo pacman -S docker-compose

systemctl enable docker.service
systemctl start docker.service

# añadimos el usuario al grupo docker
sudo usermod -aG docker daniqss

systemctl restart docker.service
```

## creación de tablas
> [!warning]
> El script esta modificado levemente para usarse con mariaDB en vez de con oracle sql

levantamos el contenedor ejecutando y nos conectamos a la base de datos con
```bash
docker compose up -d
docker exec -i mariadb mariadb -u root --password=bd bd < ./bd_docencia_completa_neutro.sql
```

## cómo correr sentencias sql
abrimos un entorno interativo con el comando de abajo y ya podemos escribit
```bash
docker exec -it mariadb mariadb -u bd --password=bd bd
```