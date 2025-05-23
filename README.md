# Aplicación de Gestion de Contactos  
Esta es una aplicación de escritorio para la gestión de contactos desarrollada en Java con Swing. Permite a los usuarios agregar, editar, eliminar y exportar contactos a un archivo CSV. Además, ofrece una visualización de estadísticas, la posibilidad de ordenar y filtrar los contactos, búsqueda avanzada, validación en tiempo real e internacionalización.  

## Características principales ##  
- **Interfaz Gráfica Intuitiva:** La interfaz está organizada en pestañas para facilitar la navegación.  
-  **Gestión de Contactos:** Permite agregar, editar, eliminar contactos con información detallada.  
-  **Tabla de Contactos:** Visualización estructurada de los contactos con opciones de ordenamiento y filtrado.  
-  **Exportación a CSV:** Facilita la exportación de los contactos a un archivo CSV para copias de seguridad o para compartir con otras aplicaciones.  
-  **Estadísticas:** Visualización de estadísticas sobre los contactos, como el total de contactos, favoritos y por catgoría.  
-  **Atajos de Teclado:** Agiliza la interacción con la aplicación mediante atajos de teclado para las acciones más comunes.    
-  **Validaciones en Tiempo Real:** Asegura la calidad de los datos ingresados mediante validaciones en tiempo real de los campos de texto.
-  **Búsqueda y Filtro:** Campo de búsqueda que filtra automáticamente la tabla de contactos.
-  **Internacionalización (i18n):** Menú para cambiar el idioma de la interfaz (Español, Inglés y Francés disponibles).
-  **Colores, Íconos y Fuentes Personalizadas:** Interfaz visualmente agradable con una clase centralizada de estilos.
-  **Layouts bien organizados:** Uso de 'GridBagLayout', 'FlowLayout', 'BorderLayout', 'GridLayout' para mantener orden y adaptabilidad.  
-  **Notificaciones en tiempor real:** para informar al usuario sin bloquear la UI.
-  **Sincronización y bloqueo de contactos:** Para evitar conflictos en edición y eliminación, incluso en varias ventanas abiertas.

## Tecnologías utilizadas ##  
- Java.
- Swing.
- MVC (Modelo-Vista-Controlador.
- Programación concurrente con SwingWorker (para tareas en segundo plano) y ExecutorService.
- Sincronización con ReentrantLock y gestión de locks multiinstancia (ContactLockManager).
- i18n (Java ResourceBundle para multilenguaje).  

## Estructura del Proyecto ##  
El proyecto está organizado de la siguiente manera:  
                                 
       /src
       
           >controlador
	   	  -BuscadorContactos.java
       	  -ConsolePerformanceTester.java
	   	  -ExportadorCSV.java
	          -Logica_ventana.java
	   	  -ValidadorContactos.java
	       >icons
	          -add.png
	          -amigos.png
	          -contactos.png
	          -default.png
	          -delete.png
	          -edit.png
	          -español.png
	          -export.png
	          -familia.png
	          -francés.png
	          -idioma.png
	          -inglés.png
	          -search.png
	          -trabajo.png 
	       >main
	          -Principal.java
	       >modelo
	          -Persona.java
	          -PersonaDAO.java
	       >util
		  -ContactLockManager.java
    	  -FileLockManager.java
	          -InternationalizationManager.java
	   	  -Notificador.java
	       >vista
	          -Colores.java
	          -Fuentes.java
	          -GraficoBarras.java
	          -IconComboItem.java
	          -IconListRender,java
	          -Ventana.java
        /src/resources
	          -messages_en.properties
	          -messages_es-properties
	          -messages_fr.properties
  
## Uso ##  
**1. Agregar Contacto:**  
- Ingrese los datos del contacto en los campos correspondientes.
- Seleccione una categoría y marque si es un contacto favorito.
- Haga clic en el botón "Agregar" o presione Ctrl + A.  
- La interfaz se realiza en segundo plano, la interfaz permanece activa.
- Si el contacto ya existe, se mostrará un mensaje de advertencia.  
  
**2. Editar un Contacto:**  
- Seleccione el contacto de la tabla.  
- Modifique los datos en los campos correspondientes.
- Haga clic en el botón "Modificar" o presione Ctrl + M.
- El sistema bloquea el contacto para evitar ediciones simultáneas (incluso entre diferentes ventanas).
- Si el contacto está bloqueado en otra ventana o instancia, se mostrará un mensaje.  
    
**3. Eliminar un contacto:**
- Seleccione el contacto de la tabla.
- Haga clic en el botón "Eliminar" o presiona Ctrl + D.
- La eliminación se realiza en segundo plano con bloqueo para evitar conflictos.  
    
**4. Exportar Contactos a CSV:**
- Haga clic en el botón "Exportar" o presione Ctrl + E.
- Seleccione la ubicación y el nombre del archivo CSV.
- La exportación se realiza en segundo plano con sincronización para evitar corrupción de datos si hay exportaciones simultáneas.  
    
**5. Busqueda de Contactos:**
- Ingrese el término de búsqueda en el campo "Buscar".
- La tabla se filtrará automáticamente.

## Atajos de Teclado ##  
- **Ctrl + A:** Agregar contacto.
- **Ctrl + D:** Eliminar contacto.
- **Ctrl + M:** Modificar contacto.
- **Ctrl + E:** Exportar contactos.

## Validaciones en Tiempo Real ##  
La aplicación incluye validaciones en tiempo real para los campos de texto:  
- **Nombre:** Solo se permiten letras y espacios.
- **Teléfono:** Solo se permiten números.
- **Email:** Formato de correo electrónico válido.

## Internacionalización ##  
La interfaz puede cambiar de idioma dinámicamente. Se implementó "ResourceBundle" y se puede extender fácilmente a otros idiomas.  
-**Idiomas disponibles:**  
- Español  
- Inglés  
- Francés  

## Sincronización y bloqueo ##  
- Uso de ReentrantLock para evitar que múltiples hilos o instancias modifiquen el mismo contacto simultaneamente.  
- Implementación de ContactLockManager para bloqueo multiinstancia, asegurando que solo una ventana de la aplicación pueda editar/eliminar un contacto a la vez.

## Instrucciones para Clonar y Ejecutar el Proyecto ##
**1.** Asegurate de tener Git instalado en tu sistema. Puedes verificarlo abriendo una terminal y ejecutando el siguiente comando:
     
	 git -- version
Si git está instalado, verás la versión correspondiente. Si no está instalado, descárgalo e instálalo desde https://git-scm.com/.    
**2.** Navega a la carpeta donde deseas clonar el proyecto.  
**3.** Haz clic derecho en la carpeta y selecciona "Open Git Bash Here". Esto abrirá una terminal de Git Bash en la ubicación seleccionada.  
**4.** Ejecuta el siguiente comando:
    
	git clone https://github.com/DarwinMendoza7/GestionContactos.git
**5.** Para importar el proyecto a Eclipse haz lo siguiente:
- Abre Eclipse.
- Selecciona File luego Import y luego Existing Projects Into Workspace y pulsa en Next.
- Navega hasta la carpeta donde clonaste el proyecto y haz clic en Finish.  

**6.** En la vista de Proyecto, encuentra la clase Principal que está en el paquete main, haz clic derecho sobre la clase y selecciona Run As y luego Java Application.

**Para clonar directamente desde Eclipse sigue estos pasos:**

**1.** Inicia Eclipse.  
**2.** Ve al menú File y selecciona la opción Import. Luego expande la carpeta Git y selecciona Projects from Git. A continuación, elige Clone URI y haz clic en Next.  
**3.** En el campo URI pega el enlace del repositorio que vas a clonar: https://github.com/DarwinMendoza7/GestionContactos.git y completa el campo de Authentication con tus datos y haz clic en Next.  
**4.** En la selección de ramas (Branch Selection), marca la rama main o master según corresponda y haz click en Next.  
**5.** En local Destination, selecciona la carpeta donde deseas clonar el proyecto y asigna un nombre al proyecto en el campo correspondiente. Luego haz clic en Next.  
**6.** En la ventana Select a wizard to use for importing projects, selecciona Import using the New Project wizard y haz clic en Finish.  
**7.** Se abrirá una ventana donde debes seleccionar el tipo de proyecto, elige Java Project en la carpeta de Java.  
**8.** Aparecerá una ventana para crear el nuevo proyecto. Ingresa el nombre del proyecto que desees, desmarca la opción que dice Use default location, y escoge la carpeta donde se clonó el proyecto. Luego haz clic en Finish.  
**9.** Por último, navega a la clase Principal que está en el paquete main, haz clic derecho sobre ella, selecciona Run As, y luego elige Java Application.  
