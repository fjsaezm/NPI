
---
title: "NUI Alhambra"
author: "Francisco Javier Sáez Maldonado"
header-includes:
  -  \usepackage[utf8]{inputenc}
  -  \usepackage[T1]{fontenc}
  -  \usepackage[sfdefault,scaled=.85, lining]{FiraSans}
  -  \usepackage{geometry}
  -  \geometry{left=3cm,right=3cm,top=3cm,bottom=3cm,headheight=1cm,headsep=0.5cm}

output:
    pdf_document
---


# Introducción

El Patronato de La Alhambra ha pedido que se realice un cambio radical de su concepto visual, sin poner limitaciones en cuanto a lo creativo y a lo económico. Por ello, trataremos de diseñar un NUI para el monumento de nuestra ciudad.

Se expondrán en este documento las ideas principales sobre nuestro proyecto, y más adelante se irán comentando las decisiones finales sobre el camino que tomará el mismo.

# La primera idea

La idea inicial que propongo pretende dar una visión de la visita al monumento que sea totalmente renovada pero que trate a la vez de no perder la esencia de visitar un lugar histórico antiguo.

Se utilizará como dispositivo unas gafas que tendrán los cristales oscuros pero permitirán la visión normal del usuario. A partir de estas gafas, se podrán tendrán diferentes funcionalidades que permitirán una visita moderna e interactiva a la Alhambra. Las gafas tendrán incorporadas (en una versión de tamaño muy reducido para mantener la estética) los siguientes dispositivos:

- Cámara de fotos y vídeo
- Micrófono
- Acelerómetro y giroscopio
- GPS


El usuario deberá llevar las gafas puestas durante el recorrido y podrá ir caminando libremente por los jardines y palacios de la Alhambra. Mientras va caminando, las gafas irán detectando mediante reconocimiento de imágenes qué zonas son las que el individuo va observando, y si es una zona conocida o emblemática, un pequeño cuadro emergent$e^{(1)}$ aparecerí$a^{(2)}$ en el campo visual del usuario, como una etiqueta que le indica qué está viendo. En ese momento, el asistente de voz indicaría al usuario lo que está viendo y le preguntaría si desea obtener más información sobre el sitio. El usuario podrá dar una respuesta de diversas maneras como afirmando por su voz con un "Sí" o "No"", o haciendo un gesto de afirmación o negación con su cabeza que el dispositivo detectará.

Nuestro dispositivo tendrá ciertos recorridos programados para distintos tipos de usuarios que pudieran pedir tener una visita más monótona, pero el uso más común sería el que acabamos de comentar. Además, el GPS de las gafas se encargaría de detectar la posición del usuario y el dispositivo podría indicarle por el asistente de voz al mismo que está cerca de ciertos monumentos y ofrecería si quiere indicarle el camino hacia los mismos. Si el usuario se niega a esto, el asistente ofrecería los nombres de algunos de los sitios algo más lejanos por si el usuario le apetece visitar algún otro lugar.

Dado que estás visitando un monumento cultural de gran importancia y con gran belleza estética, se me ocurre que se podría tener un botón en las gafas que haga una foto de lo que esté en el campo visual justamente en el momento en el que se pulsa el botón. Todas las fotos realizadas se podrían enviar tanto durante la visita como al final de la visita a un correo electrónico o a su mismo móvil utilizando el Bluetooth de forma instantánea.

Por último, el usuario tendría la posiblidad de pasar a un **modo retro** durante la visita. Este modo ofrecería una visión de la alhambra de la época, que podría ser o bien por ambos ojos, o bien por uno de ellos y el otro una vista normal para poder hacer una comparación de ambos mundos. En el modo retro, junto a los diferentes lugares aparecerían personajes de la época que narrarían información sobre el uso y otros datos del sitio en la época en la que la Alhambra era aún una pequeña ciudad.



**Aclaraciones**:
1. Este cuadro aparecería en la visión del usuario como un "bocadillo" de un cómic, y no entorpecería al usuario las vistas del lugar
2. Varios cuadros podrían convivir en la visión del usuario, si está por ejemplo en una zona ancha donde está divisando varios lugares importantes.

# Planteamiento conjunto

Uno de mis compañeros había pensado algo similar a la primera idea que propuse, por lo que no fue difícil escoger esta idea y tratar de realizar una mezcla entre las ideas que ambos habíamos aportado. Quedamos de acuerdo en que nuestro dispositivo de trabajo serían unas gafas con dispositivos internos que nos permitan realizar diferentes tipos de interacciones con el usuario. 

En concreto, nuestra principal función sería la de reconocimiento de los diferentes lugares que se encuentran en la Alhambra y ofrecimiento al usuario de información de los mismos de cara a conocer la alhambra de forma más profunda sin quedarse solo en lo espectacular de la vista. 

Ahora, para realizar esto, disponemos de nuestro teléfono móvil como dispositivo. Es por ello, que decidimos que al abrir la aplicación, el teléfono estuviera directamente en modo cámara , simulando así la vista de una persona que va andando por la Alhambra. Entonces, cuando nuestra aplicación reconociese una cierta imagen previamente fijada por nosotros (pues hacer el sistema de reconocimiento completo sería bastante más complejo), mostrara una notificación que nos preguntase si queremos obtener información sobre el lugar hacia el que se está mirando. En caso afirmativo, en un lado de nuestro movil (simulando que en las gafas intentaríamos que no ocupase toda la visión, para que el usuario pudiese seguir viendo el monumento a la vez que leyendo o recibiendo la información.

Además, tratamos de plantear la aplicación de forma que fuera lo más ligera de menús de selección posible. Es por ello que todos los procesos trataremos que estén andando al mismo tiempo y ofrezcan una buena experiencia al usuario. El siguiente proceso importante es el uso del GPS para , sin dar al usuario una guía concreta de qué camino seguir al visitar la Alhambra y dándole libertad en este aspecto, detectar la posición del mismo y descubrirle ciertos lugares que estén cerca de él y pueda visitarlos. Para ello, cuando nuestra aplicación detecte la cercanía del usuario a uno de los monumentos, una notificación aparecería al usuario indicando que está cerca de uno y preguntándole si quiere obtener alguna indicación sobre cómo llegar al mismo. Hay que tener en cuenta que si un usuario acepta o rechaza información sobre un monumento, se debe introducir este en una lista de "monumentos visitados" y "monumentos que no se desean visitar por el momento", para que la aplicación descarte este tipo de monumentos si se vuelve a pasar cerca de ellos.

Por último, pensamos que hoy día es importante también llevar recuerdos en forma de fotografías de los sitios a donde vayas. Es por ello que incorporamos una funcionalidad para que la aplicación pueda acceder a la galería y compartir fotos de diversas maneras, entre ellas, utilizando el Bluetooth del teléfono.

Se pusieron sobre la mesa por mi parte otras ideas como la de realizar las aceptaciones o negaciones de información haciendo gestos con la cabeza que el acelerómetro de las gafas pudiese detectar. Sin embargo, se acabó rechazando la propuesta ya que es algo que puede parecer antinatural en una visita a este monumento. El modo retro propuesto se discutió como una buena idea,aunque finalmente se optó por implementar otras funcionalidades porque esto requería de implementar primero el reconocimiento de imágenes, cosas que, como comentaré más adelante, no salió bien.

# Implementación final

La implementación final ha tratado de ajustarse a nuestros conocimientos en el tiempo dado. Es por ello que muchas de las funciones han tenido que verse reducidas en su implementación, tratando de ir a una aplicación útil y con un diseño lo más simple posible. Podemos resumir que la implementación final ha quedado en los siguientes puntos:

- El reconocimiento de imágenes es complejo de implementar. Aunque hicimos varios intentos de esto, requería mucho tiempo al no estar familiarizados con Android Studio, la instalación de OpenCV en el mismo, y a continuación el reconocimiento de imágenes. Es por ello que decidimos implementar un *lector de códigos QR*, donde la intención que se tiene es enunciar que la aplicación real estaría reconociendo imágenes en tiempo real (como es capaz de hacer *google Lens*), e identificando estas con los monumentos de la Alhambra.

- Dado que no disponemos de unas gafas que puedan realizar este tipo de acciones, suponemos para la implementación que nuestra pantalla táctil es un sensor táctil que tendrían las gafas en una de las patillas, y haremos ciertas acciones en nuestra pantalla táctil, como tocar para entrar en el menú, o deslizar con dos dedos a izquierda o derecha para aceptar o rechazar una petición por parte de la aplicación. 

- En la aplicación, se mostrarían la información dentro de las mismas gafas, sin embargo, lo que nosotros hemos hecho ha sido enlazar directamente códigos QR propios con páginas web de los monumentos, lo que provoca que al leer uno y realizar el gesto multitáctil, se abra el navegador y obtengamos la información desde la página web.

En cuanto al **diseño**, se ha tratado que la aplicación sea lo más simple posible, teniendo en ella los mínimos elementos que caracterizan a una aplicación corriente y tratando de que el diseño minimalista proporcione una buena sensación al usuario tanto estéticamente como en funcionalidad.
