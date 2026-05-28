Guía de Configuración y Despliegue de Infraestructura en AWS (RoomOps)

Este manual técnico describe el proceso completo desde cero para la creación, configuración y despliegue de la arquitectura web en Amazon Web Services (AWS), utilizando una base de datos relacional (RDS PostgreSQL), dos instancias de servidores virtuales (EC2) para Frontend y Backend, y las reglas necesarias en los Grupos de Seguridad.

PASO 1: Creación y Configuración de la Base de Datos (AWS RDS)
1. Crear la instancia de base de datos
Buscar el servicio RDS en la barra superior de la consola de AWS.
Hacer clic en el botón Create database (Crear base de datos).
Seleccionar el método de creación Standard create (Creación estándar).
En las opciones de motor (Engine options), seleccionar PostgreSQL.
En las plantillas (Templates), elegir Free Tier (Capa gratuita) para evitar costos asociados.
En la sección de configuración (Settings):
Asignar un identificador a la base de datos (ej. roomops-db).
Definir el nombre de usuario maestro (por defecto suele ser postgres).
Establecer y guardar en un lugar seguro la contraseña maestra.
En la sección de conectividad (Connectivity):
Configurar el acceso público (Public access) en Yes (Sí), para permitir las pruebas de conexión iniciales desde el entorno de desarrollo local.
En la configuración adicional (Additional configuration):
Definir el nombre de la base de datos inicial (ej. roomops).
Hacer clic en Create database. El proceso de aprovisionamiento puede tardar unos minutos hasta que el estado cambie a Available (Disponible).
Copiar el Endpoint (Punto de enlace) generado, el cual servirá como dirección del host en la configuración del Backend.
PASO 2: Creación de Instancias Virtuales (AWS EC2)

Se requiere la creación de dos instancias separadas con el sistema operativo Debian o Ubuntu.

1. Crear la Instancia para el Backend
Buscar el servicio EC2 en la consola de AWS.
Hacer clic en Launch instance (Lanzar instancia).
Asignar el nombre RoomOps-Backend en la sección de etiquetas.
En las imágenes de sistema operativo (Application and OS Images), seleccionar Debian (o Ubuntu).
En el tipo de instancia (Instance type), seleccionar t2.micro (o t3.micro, según disponibilidad de la capa gratuita).
En el par de claves (Key pair), seleccionar o crear una clave .pem para permitir la posterior conexión SSH mediante Remmina.
En la configuración de red (Network settings), permitir la creación de un nuevo Grupo de Seguridad llamado sg-roomops-backend.
Hacer clic en Launch instance.
2. Crear la Instancia para el Frontend
Repetir el proceso anterior haciendo clic en Launch instance.
Asignar el nombre RoomOps-Frontend.
Seleccionar la misma distribución de sistema operativo y tipo de instancia (t2.micro).
Seleccionar el mismo par de claves .pem.
En la configuración de red, permitir la creación de un nuevo Grupo de Seguridad llamado sg-roomops-frontend.
Hacer clic en Launch instance.
PASO 3: Configuración de Red y Grupos de Seguridad (Security Groups)

Es fundamental abrir los puertos correctos en AWS para permitir el flujo de datos entre el cliente, el Frontend, el Backend y la Base de Datos.

1. Configurar el Grupo de Seguridad de RDS
Ir a la sección de Security Groups dentro del menú izquierdo de EC2.
Seleccionar el grupo de seguridad asociado a la base de datos RDS.
En la pestaña Inbound rules (Reglas de entrada), hacer clic en Edit inbound rules.
Añadir una regla con los siguientes parámetros:
Type: PostgreSQL (Puerto 5432).
Source: 0.0.0.0/0 (Cualquier lugar, para desarrollo) o restringir a la IP privada de la instancia de Backend para mayor seguridad en producción.
Hacer clic en Save rules.
2. Configurar el Grupo de Seguridad del Backend (sg-roomops-backend)
Seleccionar el grupo de seguridad correspondiente al Backend.
Hacer clic en Edit inbound rules y añadir las siguientes reglas de entrada:
Regla 1 (SSH): Puerto 22 | Source: Mi IP (Para la conexión de Remmina).
Regla 2 (Custom TCP): Puerto 5000 | Source: 0.0.0.0/0 (Para permitir que el Frontend y Swagger consuman la API).
Hacer clic en Save rules.
3. Configurar el Grupo de Seguridad del Frontend (sg-roomops-frontend)
Seleccionar el grupo de seguridad correspondiente al Frontend.
Hacer clic en Edit inbound rules y añadir las siguientes reglas de entrada:
Regla 1 (SSH): Puerto 22 | Source: Mi IP.
Regla 2 (HTTP): Puerto 80 | Source: 0.0.0.0/0 (Para permitir el acceso público a la aplicación web a través del navegador).
Hacer clic en Save rules.
PASO 4: Despliegue y Ejecución del Backend (Spring Boot)
1. Preparación del código local

Antes de compilar, asegurar que el archivo application.properties o application.yml contenga el Endpoint de la RDS, las credenciales maestras configuradas en el Paso 1, y que el archivo SecurityConfig.java incluya la IP pública del Frontend en los orígenes permitidos de CORS.

Ejecutar la compilación local en la terminal del Backend:

mvn clean package
2. Despliegue en el servidor EC2

Conectarse por SSH a la instancia de Backend mediante Remmina y transferir el archivo .jar generado (ubicado en la carpeta target/) a través del panel SFTP.

Ejecutar los siguientes comandos en la terminal de la instancia EC2 para limpiar procesos previos y levantar el servicio en segundo plano:

# Liberar de forma preventiva el puerto 5000 ante cualquier ejecución colgada
sudo kill -9 $(sudo lsof -t -i:5000) 2>/dev/null || true

# Iniciar la aplicación Spring Boot en segundo plano utilizando nohup
nohup java -jar nombre-del-archivo-backend.jar > backend.log 2>&1 &

# Verificar la correcta asignación del puerto de escucha
sudo ss -tlnp | grep :5000

(Opcional: Ejecutar tail -f backend.log para visualizar los registros de inicialización de la API. Salir con Ctrl + C).

PASO 5: Despliegue y Ejecución del Frontend (Nginx + React/Vite)
1. Preparación del código local

Asegurar que el archivo de configuración del cliente (ej. api.js) apunte a la dirección IP pública y puerto 5000 de la instancia del Backend.

Ejecutar la compilación local en la terminal del Frontend:

npm run build

(Este comando genera una carpeta llamada dist en la raíz del proyecto).

2. Configuración del Servidor Web Nginx en EC2

Conectarse por SSH a la instancia de Frontend mediante Remmina y ejecutar la instalación del servidor:

# Actualizar el índice de paquetes del sistema operativo
sudo apt update

# Instalar el servidor web Nginx
sudo apt install nginx -y

# Habilitar e iniciar el servicio web
sudo systemctl enable nginx
sudo systemctl start nginx

# Otorgar privilegios al usuario actual sobre la carpeta raíz de despliegue
sudo chown -R $USER:$USER /var/www/html

# Eliminar el archivo por defecto de bienvenida de Nginx
rm -f /var/www/html/index.nginx-debian.html
3. Transferencia de archivos estáticos

Mediante el panel SFTP de Remmina, entrar a la carpeta local dist, seleccionar todo su contenido interno (los archivos sueltos y la carpeta assets) y arrastrarlos directamente hacia el directorio remoto /var/www/html/.

4. Evitar el error 404 ante recargas de rutas virtuales

Editar el archivo de configuración de Nginx para redirigir el tráfico dinámico al punto de entrada de la SPA:

sudo nano /etc/nginx/sites-available/default

Localizar el bloque de directivas location / y sustituir el valor =404 por /index.html. Modificar la estructura para que se lea exactamente así:

location / {
        try_files $uri $uri/ /index.html;
}

(Guardar la edición mediante la combinación de teclas Ctrl + O, confirmar con Enter y salir del editor con Ctrl + X).

5. Reinicio y confirmación del Frontend

Validar la configuración y aplicar los cambios estructurales en el servidor web:

# Validar la sintaxis del archivo modificado
sudo nginx -t

# Reiniciar el servicio web para aplicar las nuevas rutas
sudo systemctl restart nginx

# Comprobar el estado activo y saludable del servicio
sudo systemctl status nginx

A partir de este punto, la arquitectura se encuentra totalmente desplegada y en funcionamiento en entorno de producción.
