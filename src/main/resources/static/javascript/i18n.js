/* LVL ESSENCE · Internacionalización de la interfaz pública (ES/EN).
 * El catálogo y los campos introducidos por usuarios NO se traducen: son datos.
 * La tienda es una SPA con fragmentos cargados por AJAX/Mustache, por lo que
 * traducimos sus textos de interfaz sin duplicar plantillas ni alterar las APIs.
 */
(function () {
    "use strict";

    const CLAVE = "lvl-essence-idioma";
    const diccionario = {
        "Identifícate": "Sign in",
        "Mostrar imagen": "Show image",
        "Progreso de compra": "Checkout progress",
        "Envío": "Shipping",
        "Envoltorio": "Gift wrap",
        "Confirmación": "Confirmation",
        "Carrito": "Bag",
        "Descubre nuestra selección de perfumes.": "Discover our selection of perfumes.",
        "Amaderados": "Woody",
        "Cítricos": "Citrus",
        "Orientales": "Oriental",
        "Selecciones destacadas": "Featured selections",
        "Último añadido": "Latest arrival",
        "Ver categoría": "View category",
        "Ver más": "See more",
        "Productos sugeridos": "Suggested products",
        "Ver todos los resultados": "See all results",
        "Buscando...": "Searching...",
        "Sin resultados.": "No results.",
        "Error al buscar. Puedes realizar la búsqueda completa.": "Search failed. You can still search the full catalogue.",
        "Todos los campos son obligatorios.": "All fields are required.",
        "Mostrar contraseña": "Show password",
        "¿No tienes cuenta?": "Don't have an account?",
        "¿Ya tienes cuenta?": "Already have an account?",
        "Enviando...": "Submitting...",
        "No se pudo conectar. Inténtalo de nuevo.": "Could not connect. Please try again.",
        "Notificaciones": "Notifications",
        "Cerrar notificación": "Dismiss notification",
        "Confirmar acción": "Confirm action",
        "¿Deseas continuar?": "Do you want to continue?",
        "Confirmar": "Confirm",
        "Eliminar": "Remove",
        "Escribe el texto para confirmar": "Type the text to confirm",
        "Eliminado de favoritos.": "Removed from wishlist.",
        "No se pudo cargar el carrito.": "Could not load your bag.",
        "Resumen de compra": "Order summary",
        "Unidades": "Items",
        "Continuar compra": "Continue checkout",
        "Método de pago simulado": "Simulated payment method",
        "Datos de facturación": "Billing details",
        "Volver a favoritos": "Back to wishlist",
        "Explorar catalogo": "Explore catalogue",
        "Reintentar": "Try again",
        "Notas de fragancia": "Fragrance notes",
        "Cargando favoritos...": "Loading wishlist...",
        "No se pudieron cargar los favoritos.": "Could not load your wishlist.",
        "Caracteristicas del perfume": "Perfume characteristics",
        "Imagenes del perfume": "Perfume images",
        "Reducir cantidad": "Decrease quantity",
        "Aumentar cantidad": "Increase quantity",
        "Imagen anterior": "Previous image",
        "Imagen siguiente": "Next image",
        "Cerrar menu": "Close menu",
        "Valoraciones": "Ratings",
        "votos": "votes",
        "Cargando valoraciones...": "Loading ratings...",
        "Sin valoraciones": "No ratings yet",
        "Tu puntuacion": "Your rating",
        "Selecciona una puntuacion": "Select a rating",
        "Guardar puntuacion": "Save rating",
        "Puntuacion guardada.": "Rating saved.",
        "Inicia sesion para valorar.": "Sign in to rate.",
        "No se pudo guardar la puntuacion.": "Could not save your rating.",
        "No se pudieron cargar las valoraciones.": "Could not load ratings.",
        "Seleccion general": "General selection",
        "Afinidad con este perfume": "Similar to this perfume",
        "Iniciar sesion": "Log in",
        "Cerrar sesion": "Log out",
        "Navegacion principal": "Main navigation",
        "Menu": "Menu",
        "Tienda": "Shop",
        "Buscar": "Search",
        "Buscar perfume": "Search perfumes",
        "Favoritos": "Wishlist",
        "Comprar por categoria": "Shop by category",
        "Mejor valorados": "Top rated",
        "Aun no hay valoraciones disponibles.": "Ratings are not available yet.",
        "Novedades": "New arrivals",
        "Tambien te puede gustar": "You may also like",
        "Ver mas": "See more",
        "Sugerencias anteriores": "Previous suggestions",
        "Siguientes sugerencias": "Next suggestions",
        "Anadir a favoritos": "Add to wishlist",
        "Eliminar de favoritos": "Remove from wishlist",
        "Anadido a favoritos.": "Added to wishlist.",
        "Inicia sesion para guardar favoritos.": "Sign in to save favorites.",
        "Inicia sesion para ver tus favoritos.": "Sign in to view your wishlist.",
        "No se pudo guardar el favorito.": "Could not save this favorite.",
        "No se pudo eliminar el favorito.": "Could not remove this favorite.",
        "No se pudieron cargar los favoritos.": "Could not load your wishlist.",
        "Tu lista de favoritos esta vacia.": "Your wishlist is empty.",
        "Notas olfativas": "Fragrance notes",
        "Notas presentes en nuestro catalogo.": "Notes found in our catalog.",
        "No hay notas disponibles.": "No notes available.",
        "No se pudieron cargar las notas.": "Could not load the notes.",
        "No hay perfumes disponibles.": "No perfumes available.",
        "No se pudo cargar la tienda.": "Could not load the shop.",
        // Cabecera, pie y modal
        "Perfumes": "Perfumes",
        "Iniciar sesión": "Log in",
        "Registrarme": "Sign up",
        "Contacto": "Contact",
        "Sobre nosotros": "About us",
        "Mis pedidos": "My orders",
        "Mis datos": "My details",
        "Cerrar sesión": "Log out",
        "Mi cuenta": "My account",
        "LVL ESSENCE, ir al inicio": "LVL ESSENCE, go to home",
        "Navegación principal": "Main navigation",
        "Ver carrito": "View cart",
        "Abrir opciones de cuenta": "Open account menu",
        "Información legal y de contacto": "Legal and contact information",
        "Política de privacidad": "Privacy policy",
        "Política de devoluciones": "Returns policy",
        "Términos y servicios": "Terms and conditions",
        "Administración": "Administration",
        "Una experiencia de perfumería selecta.": "A refined fragrance experience.",
        "Proyecto académico en desarrollo. Los textos legales son borradores pendientes de revisión.":
            "Academic project in development. Legal texts are drafts awaiting review.",
        "Idioma": "Language",
        "Cambiar idioma": "Change language",
        "Elige tu idioma": "Choose your language",
        "Selecciona el idioma de la tienda.": "Choose the language of the store.",
        "Español": "Spanish",
        "Inglés": "English",
        "Cerrar": "Close",

        // Portada y catálogo
        "Bienvenido a LVL ESSENCE": "Welcome to LVL ESSENCE",
        "Perfumería selecta": "Fine perfumery",
        "Aromas elegidos para dejar memoria": "Scents chosen to leave a lasting impression",
        "Explora una selección de perfumes para descubrir tu próxima fragancia.":
            "Explore our selection of perfumes and discover your next fragrance.",
        "Explorar perfumes": "Explore perfumes",
        "LVL ESSENCE · Nuestra selección": "LVL ESSENCE · Our selection",
        "Explora nuestros perfumes": "Explore our perfumes",
        "Encuentra tu fragancia por nombre, categoría o precio.":
            "Find your fragrance by name, category or price.",
        "Buscar y filtrar perfumes": "Search and filter perfumes",
        "Catálogo de perfumes": "Perfume catalogue",
        "Buscar perfume": "Search perfumes",
        "Ej.: Sauvage, Invictus...": "e.g. Sauvage, Invictus...",
        "Categoría": "Category",
        "Todas": "All",
        "Precio mín. (€)": "Min. price (€)",
        "Precio máx. (€)": "Max. price (€)",
        "Sin límite": "No limit",
        "Ordenar por": "Sort by",
        "Nombre: A-Z": "Name: A–Z",
        "Nombre: Z-A": "Name: Z–A",
        "Precio: menor a mayor": "Price: low to high",
        "Precio: mayor a menor": "Price: high to low",
        "Más recientes": "Newest first",
        "Aplicar filtros": "Apply filters",
        "Limpiar": "Clear",
        "Cargando perfumes...": "Loading perfumes...",
        "10 perfumes por página": "10 perfumes per page",
        "Perfumes encontrados": "Perfumes found",
        "Páginas del catálogo": "Catalogue pages",
        "Anterior": "Previous",
        "Siguiente": "Next",
        "Página anterior": "Previous page",
        "Página siguiente": "Next page",
        "No hay perfumes con esos filtros. Prueba con otros criterios.":
            "No perfumes match these filters. Try different criteria.",
        "No se pudo cargar el catálogo": "Could not load the catalogue",
        "No se pudo cargar el catálogo. Actualiza la página e inténtalo de nuevo.":
            "Could not load the catalogue. Refresh the page and try again.",
        "Las categorías no están disponibles ahora.": "Categories are not available right now.",
        "Revisa los filtros: los precios deben ser válidos y el mínimo no puede superar al máximo.":
            "Check your filters: prices must be valid and the minimum cannot exceed the maximum.",
        "Error al consultar los perfumes. Inténtalo de nuevo.":
            "Could not load perfumes. Please try again.",
        "Introduce precios válidos: el mínimo no puede superar al máximo.":
            "Enter valid prices: the minimum cannot exceed the maximum.",
        "Ver detalles": "View details",
        "Volver al catalogo": "Back to catalog",
        "Larga duracion": "Lasting impression",
        "Una inspiracion que acompana tus momentos.": "An inspiration for your moments.",
        "Aceites finos": "Fine oils",
        "Un homenaje al arte de la perfumeria.": "A tribute to the art of perfumery.",
        "Mezcla elegante": "Elegant blend",
        "Una expresion de estilo personal.": "An expression of personal style.",
        "Lujo diario": "Everyday luxury",
        "Un gesto especial para cada dia.": "A special touch for every day.",
        "Tamano del frasco": "Bottle size",
        "No especificado": "Not specified",
        "Imagen no disponible": "Image unavailable",
        "Piramide olfativa": "Fragrance notes",
        "Notas de salida": "Top notes",
        "Notas de corazon": "Heart notes",
        "Notas de fondo": "Base notes",
        "No especificadas": "Not specified",
        "Compra del perfume": "Purchase perfume",
        "Precio original": "Original price",
        "Precio final": "Final price",
        "No se pudieron cargar los detalles del perfume.": "Could not load the perfume details.",
        "Agregar al carrito": "Add to cart",
        "Cantidad": "Quantity",
        "Sin categoría": "Uncategorised",

        // Registro e identificación
        "Cuenta": "Account",
        "Crear cuenta": "Create account",
        "Introduce tus datos para registrarte en la tienda.":
            "Enter your details to register at the store.",
        "Nombre": "First name",
        "Apellidos": "Last name",
        "Correo electrónico": "Email address",
        "Contraseña": "Password",
        "Teléfono": "Phone number",
        "Tu nombre": "Your first name",
        "Tus apellidos": "Your last name",
        "correo@ejemplo.com": "email@example.com",
        "Accede a tu cuenta de LVL ESSENCE.": "Sign in to your LVL ESSENCE account.",
        "Te has registrado correctamente": "You have registered successfully",
        "Este correo electrónico ya está registrado.": "This email address is already registered.",
        "No se pudo completar el registro.": "Registration could not be completed.",
        "Bienvenido a LVL ESSENCE. Has iniciado sesión.":
            "Welcome to LVL ESSENCE. You are now logged in.",
        "Credenciales incorrectas.": "Incorrect credentials.",
        "Correo o contraseña incorrectos.": "Incorrect email or password.",
        "Error de conexión durante la identificación.": "Connection error while logging in.",
        "No se pudo cerrar la sesión. Inténtalo de nuevo.":
            "Could not log out. Please try again.",

        // Carrito y compra
        "Tu selección": "Your selection",
        "Carrito de compra": "Shopping bag",
        "Revisa los perfumes y sus cantidades.": "Review the perfumes and quantities.",
        "Subtotal": "Subtotal",
        "Eliminar perfume": "Remove perfume",
        "Tu carrito está vacío.": "Your bag is empty.",
        "Total": "Total",
        "Realizar pedido": "Checkout",
        "Perfume añadido al carrito": "Perfume added to your bag",
        "Perfume eliminado del carrito": "Perfume removed from your bag",
        "Cantidad actualizada": "Quantity updated",
        "Debes indicar una cantidad válida.": "Enter a valid quantity.",
        "Debes identificarte antes de comprar.": "Please log in before shopping.",
        "No se pudo añadir el perfume.": "Could not add the perfume.",
        "Debes identificarte para ver tu carrito.": "Please log in to view your bag.",
        "¿Eliminar este perfume del carrito?": "Remove this perfume from your bag?",
        "No se pudo eliminar el perfume.": "Could not remove the perfume.",
        "La cantidad mínima es 1.": "The minimum quantity is 1.",
        "No se pudo modificar la cantidad.": "Could not change the quantity.",
        "Tu carrito está vacío.": "Your bag is empty.",
        "Error al cargar el paso 1.": "Could not load step 1.",
        "No se pudo acceder al checkout.": "Could not open checkout.",
        "Datos de envío": "Shipping details",
        "Paso 1 de 4": "Step 1 of 4",
        "Nombre completo": "Full name",
        "Dirección": "Address",
        "Provincia": "Province / region",
        "Ciudad": "City",
        "Código postal": "Postal code",
        "Instrucciones de entrega (opcional)": "Delivery instructions (optional)",
        "Por ejemplo: llamar al timbre": "For example: ring the doorbell",
        "Continuar al pago": "Continue to payment",
        "Datos de pago": "Payment details",
        "Paso 2 de 4": "Step 2 of 4",
        "Pago simulado para esta práctica. No se realizará ningún cobro. No introduzcas datos reales de tarjeta.":
            "This is a simulated payment for this project. No charge will be made. Do not enter real card details.",
        "Tipo de tarjeta de prueba": "Test card type",
        "Seleccionar": "Select",
        "Visa (simulada)": "Visa (simulated)",
        "Mastercard (simulada)": "Mastercard (simulated)",
        "Titular ficticio": "Fictitious cardholder",
        "Cliente de prueba": "Test customer",
        "Email de facturación": "Billing email",
        "Introduce un correo completo, por ejemplo nombre@dominio.com":
            "Enter a complete email address, for example name@domain.com",
        "Continuar": "Continue",
        "Personaliza tu pedido": "Personalise your order",
        "Paso 3 de 4": "Step 3 of 4",
        "¿Quieres que preparemos tu pedido con envoltorio de regalo?":
            "Would you like gift wrapping for your order?",
        "Sí, quiero envoltorio de regalo": "Yes, I want gift wrapping",
        "No, es para uso personal": "No, it's for personal use",
        "Ver resumen del pedido": "Review order",
        "Resumen del pedido": "Order summary",
        "Paso 4 de 4 - Confirmación": "Step 4 of 4 – Confirmation",
        "Perfumes seleccionados": "Selected perfumes",
        "El carrito está vacío.": "The bag is empty.",
        "Pago simulado": "Simulated payment",
        "Personalización": "Personalisation",
        "Confirmar pedido": "Place order",
        "¡Pedido confirmado!": "Order confirmed!",
        "Tu compra de prueba se ha registrado.": "Your test purchase has been recorded.",
        "Gracias por elegir LVL ESSENCE.": "Thank you for choosing LVL ESSENCE.",
        "No se pudieron guardar los datos.": "Could not save the details.",
        "No se pudo cargar el paso 3.": "Could not load step 3.",
        "El carrito está vacío.": "The bag is empty.",
        "No se pudo cargar el resumen.": "Could not load the summary.",
        "No se pudo confirmar el pedido.": "Could not confirm your order.",
        "Revisa los datos obligatorios y su formato.":
            "Check the required fields and their format.",
        "Inicia sesión para continuar.": "Please log in to continue.",
        "La sesión de seguridad ha caducado. Actualiza la página.":
            "Your security session has expired. Refresh the page.",
        "Completa todos los pasos y comprueba que el carrito no esté vacío.":
            "Complete every step and check that your bag is not empty.",
        "No se pudo completar la operación.": "Could not complete the operation.",
        "No se pudo completar la operación": "Could not complete the operation",
        "Debes iniciar sesión": "You must log in",
        "El código postal debe tener 5 dígitos": "The postal code must contain 5 digits",
        "Email de facturación no válido": "Invalid billing email address",
        "Debes completar todos los pasos del checkout": "You must complete all checkout steps",
        "No puedes confirmar un carrito vacío": "You cannot confirm an empty bag",
        "Respuesta no válida": "Invalid response",
        "Cantidad de producto incorrecta": "Invalid product quantity",
        "El usuario tiene varios pedidos en proceso": "The user has multiple orders in progress",
        "El usuario no existe": "The user does not exist",

        // Páginas de cuenta pendientes
        "Área de cliente": "Customer area",
        "Esta sección está pendiente de implementación en la tienda pública.":
            "This section has not yet been implemented in the public store.",
        "Volver al catálogo": "Back to catalogue",
        "No se pudo cargar esta página.": "Could not load this page.",

        // Contenido informativo y legal (borradores existentes; traducción también provisional)
        "Atención al cliente": "Customer support",
        "¿Tienes alguna pregunta sobre la tienda o nuestras fragancias? Queremos que encuentres la información de forma sencilla.":
            "Have a question about the store or our fragrances? We want information to be easy to find.",
        "Canal de atención": "Contact channel",
        "El correo electrónico de contacto todavía no está configurado. Antes de publicar la tienda se añadirá aquí un canal de atención verificado.":
            "A contact email address has not yet been configured. A verified contact channel must be added before launch.",
        "Esta página está preparada como estructura de contacto. Actualmente no existe un formulario conectado a un servicio de envío, por lo que no se recogerán mensajes desde aquí.":
            "This page is a contact placeholder. There is currently no form connected to a message service, so messages cannot be submitted here.",
        "Nuestra propuesta": "Our approach",
        "LVL ESSENCE es un proyecto de tienda online orientado a ofrecer una experiencia clara y cuidada para descubrir perfumes.":
            "LVL ESSENCE is an online perfume store project designed to provide a clear, carefully crafted fragrance discovery experience.",
        "El catálogo": "The catalogue",
        "Organizamos los perfumes por su categoría y características para facilitar la búsqueda. Puedes ordenar por precio, consultar ofertas y acceder a los detalles de cada fragancia.":
            "We organise perfumes by category and characteristics to make them easier to find. You can sort by price, explore offers and view each fragrance's details.",
        "La experiencia": "The experience",
        "El diseño combina una presentación elegante con herramientas prácticas de búsqueda, carrito y compra de demostración.":
            "The design combines an elegant presentation with practical search, shopping bag and demo checkout features.",
        "LVL ESSENCE es actualmente un proyecto académico. Este texto describe la propuesta del sitio; no afirma la existencia de una empresa comercial constituida.":
            "LVL ESSENCE is currently an academic project. This text describes the site, not an established commercial company.",
        "Ver catálogo": "View catalogue",
        "Información legal": "Legal information",
        "Borrador para desarrollo.": "Development draft.",
        "Esta página no constituye una política legal definitiva. Debe revisarse y completarse antes de aceptar usuarios reales o publicar la tienda.":
            "This is not a final legal policy. It must be reviewed and completed before accepting real users or publishing the store.",
        "Datos utilizados en el proyecto": "Data used in this project",
        "La aplicación dispone de registro e inicio de sesión de usuarios, carrito y pedidos. Estos procesos utilizan datos de cuenta y la información introducida en las compras de demostración.":
            "The application provides user registration, login, shopping bags and orders. These processes use account data and information entered during demo purchases.",
        "Información pendiente": "Information still required",
        "Identidad y datos de contacto del responsable.": "Identity and contact details of the data controller.",
        "Finalidades y bases jurídicas de cada tratamiento.": "Purposes and legal bases for each processing activity.",
        "Plazos de conservación y destinatarios.": "Retention periods and recipients.",
        "Derechos y procedimiento para ejercerlos.": "Rights and how to exercise them.",
        "Información sobre cookies y medidas de seguridad aplicables.":
            "Information about cookies and applicable security measures.",
        "Volver al inicio": "Back to home",
        "No se están definiendo aquí plazos, condiciones ni derechos contractuales reales. Esta política debe completarse antes de vender productos.":
            "No real contractual terms, deadlines or rights are defined here. This policy must be completed before selling products.",
        "Contenido que habrá que especificar": "Information that must be specified",
        "Cómo solicitar una devolución y por qué canal.": "How and where to request a return.",
        "Plazos aplicables y posibles excepciones legalmente justificadas.":
            "Applicable time limits and any legally justified exceptions.",
        "Condiciones de presentación y estado de los productos.":
            "Condition and presentation requirements for products.",
        "Costes de transporte y procedimiento de reembolso.":
            "Shipping costs and refund procedure.",
        "Tratamiento de incidencias o artículos defectuosos.":
            "How issues and defective goods are handled.",
        "El checkout actual es una demostración y no constituye un sistema de venta real con pagos procesados.":
            "The current checkout is a demo and does not represent a live sales or payment processing system.",
        "Ir a contacto": "Go to contact",
        "Estos apartados son una guía de contenido pendiente, no condiciones vinculantes de una tienda publicada.":
            "These sections are a guide to content still required, not binding terms of a published store.",
        "Ámbito del servicio": "Scope of the service",
        "LVL ESSENCE muestra un catálogo de perfumes, permite crear cuentas y simular pedidos con un proceso de pago de demostración.":
            "LVL ESSENCE displays a perfume catalogue, allows account creation and simulates orders with a demo payment flow.",
        "Puntos pendientes para una publicación real": "Requirements before a real launch",
        "Identificación del prestador y medios de contacto.":
            "Seller identification and contact details.",
        "Condiciones de compra, disponibilidad y precios finales.":
            "Purchase terms, availability and final prices.",
        "Formas de pago realmente aceptadas y facturación.":
            "Actual accepted payment methods and invoicing.",
        "Envíos, entregas, devoluciones y garantías legales.":
            "Shipping, delivery, returns and legal guarantees.",
        "Responsabilidades y mecanismos de resolución de incidencias.":
            "Responsibilities and dispute resolution mechanisms."
    };

    const japones = {
        "Hola,": "こんにちは、", "Identifícate": "ログイン", "Carrito": "カート", "Inicio": "ホーム", "Tienda": "ショップ", "Sobre nosotros": "私たちについて", "Notas": "香りのノート",
        "Buscar": "検索", "Buscar perfume": "香水を検索", "Cuenta": "アカウント", "Favoritos": "お気に入り", "Ver carrito": "カートを見る",
        "Iniciar sesion": "ログイン", "Iniciar sesión": "ログイン", "Registrarme": "新規登録", "Cerrar sesion": "ログアウト", "Cerrar sesión": "ログアウト",
        "Idioma": "言語", "Cambiar idioma": "言語を変更", "Elige tu idioma": "言語を選択", "Selecciona el idioma de la tienda.": "ショップの言語を選択してください。", "Cerrar": "閉じる",
        "Menu": "メニュー", "Cerrar menu": "メニューを閉じる", "Navegacion principal": "メインナビゲーション", "Navegación principal": "メインナビゲーション",
        "Descubre nuestra selección de perfumes.": "厳選された香水をご覧ください。", "Amaderados": "ウッディ", "Cítricos": "シトラス", "Orientales": "オリエンタル", "Selecciones destacadas": "おすすめコレクション", "Último añadido": "新着アイテム", "Ver categoría": "カテゴリーを見る", "Ver más": "もっと見る",
        "Bienvenido a LVL ESSENCE": "LVL ESSENCEへようこそ", "Perfumería selecta": "厳選されたフレグランス", "Aromas elegidos para dejar memoria": "記憶に残る、選び抜かれた香り", "Explora una selección de perfumes para descubrir tu próxima fragancia.": "厳選された香水から、次のお気に入りの香りを見つけましょう。", "Explorar perfumes": "香水を見る",
        "Productos sugeridos": "おすすめ商品", "Ver todos los resultados": "すべての検索結果を見る", "Buscando...": "検索中...", "Sin resultados.": "該当する商品はありません。", "Error al buscar. Puedes realizar la búsqueda completa.": "検索に失敗しました。通常の検索をお試しください。",
        "Categoría": "カテゴリー", "Todas": "すべて", "Precio mín. (€)": "最低価格 (€)", "Precio máx. (€)": "最高価格 (€)", "Sin límite": "指定なし", "Ordenar por": "並び替え", "Nombre: A-Z": "名前順 A–Z", "Nombre: Z-A": "名前順 Z–A", "Precio: menor a mayor": "価格の安い順", "Precio: mayor a menor": "価格の高い順", "Más recientes": "新着順", "Aplicar filtros": "絞り込む", "Limpiar": "クリア", "Cargando perfumes...": "香水を読み込み中...", "Anterior": "前へ", "Siguiente": "次へ", "Página anterior": "前のページ", "Página siguiente": "次のページ", "Ver detalles": "詳細を見る", "Agregar al carrito": "カートに入れる", "Cantidad": "数量", "Imagen no disponible": "画像はありません",
        "Notas de fragancia": "香りのノート", "Notas olfativas": "香りのノート", "Notas de salida": "トップノート", "Notas de corazon": "ミドルノート", "Notas de fondo": "ベースノート", "No especificadas": "未指定", "No especificado": "未指定", "Precio original": "通常価格", "Precio final": "販売価格", "Volver al catalogo": "ショップに戻る", "Anadir a favoritos": "お気に入りに追加", "Eliminar de favoritos": "お気に入りから削除", "Cargando favoritos...": "お気に入りを読み込み中...", "Valoraciones": "レビュー", "Sin valoraciones": "まだ評価はありません", "Tu puntuacion": "あなたの評価", "Guardar puntuacion": "評価を保存", "Reducir cantidad": "数量を減らす", "Aumentar cantidad": "数量を増やす", "Imagen anterior": "前の画像", "Imagen siguiente": "次の画像",
        "Tu selección": "選択した商品", "Carrito de compra": "ショッピングカート", "Subtotal": "小計", "Total": "合計", "Tu carrito está vacío.": "カートは空です。", "Realizar pedido": "購入手続きへ", "Continuar compra": "購入手続きを続ける", "Eliminar perfume": "商品を削除", "Resumen de compra": "注文内容", "Unidades": "点数", "Datos de envío": "配送先情報", "Paso 1 de 4": "ステップ 1 / 4", "Nombre completo": "氏名", "Dirección": "住所", "Provincia": "都道府県・地域", "Ciudad": "市区町村", "Código postal": "郵便番号", "Continuar al pago": "支払いへ進む", "Datos de pago": "お支払い情報", "Paso 2 de 4": "ステップ 2 / 4", "Continuar": "続ける", "Personaliza tu pedido": "ご注文の設定", "Paso 3 de 4": "ステップ 3 / 4", "Ver resumen del pedido": "注文内容を確認", "Resumen del pedido": "注文内容", "Paso 4 de 4 - Confirmación": "ステップ 4 / 4 - 確認", "Confirmar pedido": "注文を確定", "¡Pedido confirmado!": "注文を受け付けました！", "Pago simulado": "模擬決済", "Personalización": "カスタマイズ",
        "Crear cuenta": "アカウント作成", "Nombre": "名", "Apellidos": "姓", "Correo electrónico": "メールアドレス", "Contraseña": "パスワード", "Teléfono": "電話番号", "Mostrar contraseña": "パスワードを表示", "Enviando...": "送信中...", "Credenciales incorrectas.": "認証情報が正しくありません。", "Correo o contraseña incorrectos.": "メールアドレスまたはパスワードが正しくありません。", "Todos los campos son obligatorios.": "すべての項目を入力してください。",
        "Notificaciones": "お知らせ", "Cerrar notificación": "通知を閉じる", "Confirmar acción": "操作の確認", "¿Deseas continuar?": "続行しますか？", "Confirmar": "確認", "Cancelar": "キャンセル", "Eliminar": "削除", "Reintentar": "再試行", "Contacto": "お問い合わせ", "Política de privacidad": "プライバシーポリシー", "Política de devoluciones": "返品ポリシー", "Términos y servicios": "利用規約", "Administración": "管理画面", "Volver al inicio": "ホームに戻る",
        "Progreso de compra": "購入手続きの進行状況", "Envío": "配送", "Envoltorio": "ギフト包装", "Confirmación": "確認", "Mostrar imagen": "画像を表示", "votos": "件の評価"
    };

    // Las plantillas de Mustache pueden mezclar etiquetas con datos variables.
    // Traducimos únicamente el prefijo conocido, conservando el dato dinámico.
    const prefijos = [
        ["Marca:", "Brand:"],
        ["Precio final:", "Final price:"],
        ["Cantidad:", "Quantity:"],
        ["Subtotal:", "Subtotal:"],
        ["Total:", "Total:"],
        ["Nombre:", "Name:"],
        ["Dirección:", "Address:"],
        ["Provincia:", "Province / region:"],
        ["Ciudad:", "City:"],
        ["Código postal:", "Postal code:"],
        ["Instrucciones:", "Instructions:"],
        ["Tipo:", "Type:"],
        ["Titular ficticio:", "Fictitious cardholder:"],
        ["Email de facturación:", "Billing email:"],
        ["Envoltorio de regalo:", "Gift wrapping:"],
        ["Hola,", "Hello,"],
        ["Sí", "Yes"],
        ["No", "No"]
    ];

    const originalesTexto = new WeakMap();
    const originalesAtributos = new WeakMap();
    let idioma = "es";
    try {
        const guardado = localStorage.getItem(CLAVE);
        if (guardado === "en" || guardado === "ja") idioma = guardado;
    } catch (_) { /* Navegadores sin almacenamiento: solo sesión actual. */ }

    function traducirFrase(frase) {
        if (idioma === "es") return frase;
        const limpia = frase.replace(/\s+/g, " ").trim();
        if (idioma === "ja") {
            if (Object.prototype.hasOwnProperty.call(japones, limpia)) return japones[limpia];
            const cantidad = limpia.match(/^(\d+) (?:perfume encontrado|perfumes encontrados)$/);
            if (cantidad) return cantidad[1] + "件の香水が見つかりました";
            const paginaJa = limpia.match(/^Ir a la página (\d+)$/);
            if (paginaJa) return paginaJa[1] + "ページへ";
            return frase;
        }
        if (Object.prototype.hasOwnProperty.call(diccionario, limpia)) return diccionario[limpia];
        // Textos generados: «11 perfumes encontrados», «1 perfume encontrado», etc.
        const recuento = limpia.match(/^(\d+) (perfume encontrado|perfumes encontrados)$/);
        if (recuento) return recuento[1] + (recuento[1] === "1" ? " perfume found" : " perfumes found");
        const unitario = limpia.match(/^(.+ EUR) \/ unidad$/);
        if (unitario) return unitario[1] + " / unit";
        const obligatorio = limpia.match(/^(.+) es obligatorio$/);
        if (obligatorio) return (diccionario[obligatorio[1]] || obligatorio[1]) + " is required";
        const pagina = limpia.match(/^Ir a la página (\d+)$/);
        if (pagina) return "Go to page " + pagina[1];
        for (const [es, en] of prefijos) {
            if (limpia === es) return en;
            if (limpia.startsWith(es + " ")) return en + limpia.slice(es.length);
        }
        return frase;
    }

    // Evita traducir nombres, marcas, descripciones y categorías que proceden de H2.
    function esDatoDeCatalogo(elemento) {
        return elemento && elemento.closest && elemento.closest(
            "script,style,code,pre,[data-no-traducir],.product-name,.product-brand,.product-meta,#cliente_identificado"
        );
    }

    function traducirNodo(nodo) {
        if (!nodo || !nodo.parentElement || esDatoDeCatalogo(nodo.parentElement)) return;
        const actual = nodo.nodeValue;
        if (!actual.trim()) return;
        let registro = originalesTexto.get(nodo);
        if (!registro || (actual !== registro.ultimo && actual !== registro.original)) {
            registro = {original: actual, ultimo: actual};
            originalesTexto.set(nodo, registro);
        }
        const limpio = registro.original.trim();
        const traducido = traducirFrase(limpio);
        const esperado = idioma !== "es" && traducido !== limpio
            ? registro.original.replace(limpio, traducido)
            : registro.original;
        registro.ultimo = esperado;
        if (actual !== esperado) nodo.nodeValue = esperado;
    }

    function traducirAtributos(elemento) {
        if (!elemento || !elemento.hasAttribute || esDatoDeCatalogo(elemento)) return;
        const atributos = ["placeholder", "aria-label", "title"];
        let registro = originalesAtributos.get(elemento);
        if (!registro) { registro = {}; originalesAtributos.set(elemento, registro); }
        for (const atributo of atributos) {
            if (!elemento.hasAttribute(atributo)) continue;
            const actual = elemento.getAttribute(atributo);
            let dato = registro[atributo];
            if (!dato || (actual !== dato.ultimo && actual !== dato.original)) {
                dato = {original: actual, ultimo: actual};
                registro[atributo] = dato;
            }
            const esperado = traducirFrase(dato.original);
            dato.ultimo = esperado;
            if (actual !== esperado) elemento.setAttribute(atributo, esperado);
        }
    }

    function aplicar(raiz) {
        if (!raiz) return;
        if (raiz.nodeType === Node.TEXT_NODE) { traducirNodo(raiz); return; }
        if (raiz.nodeType !== Node.ELEMENT_NODE && raiz.nodeType !== Node.DOCUMENT_NODE) return;
        if (raiz.nodeType === Node.ELEMENT_NODE) traducirAtributos(raiz);
        const arbol = document.createTreeWalker(raiz, NodeFilter.SHOW_ELEMENT | NodeFilter.SHOW_TEXT);
        let nodo;
        while ((nodo = arbol.nextNode())) {
            if (nodo.nodeType === Node.TEXT_NODE) traducirNodo(nodo);
            else traducirAtributos(nodo);
        }
    }

    function actualizarSelector() {
        document.documentElement.lang = idioma;
        const etiqueta = document.getElementById("etiqueta_idioma");
        if (etiqueta) etiqueta.textContent = idioma.toUpperCase();
        document.querySelectorAll("[data-idioma]").forEach(function (boton) {
            boton.setAttribute("aria-pressed", String(boton.dataset.idioma === idioma));
        });
    }

    function establecerIdioma(nuevo) {
        if (nuevo !== "es" && nuevo !== "en" && nuevo !== "ja") return;
        idioma = nuevo;
        try { localStorage.setItem(CLAVE, idioma); } catch (_) { /* no disponible */ }
        actualizarSelector();
        aplicar(document.body);
        document.dispatchEvent(new CustomEvent("lvl:idioma-cambiado", {detail: {idioma: idioma}}));
    }

    function iniciar() {
        actualizarSelector();
        aplicar(document.body);
        // Los fragmentos AJAX y las tarjetas Mustache llegan después del inicio.
        const observer = new MutationObserver(function (mutaciones) {
            for (const m of mutaciones) {
                if (m.type === "characterData") traducirNodo(m.target);
                else if (m.type === "attributes") traducirAtributos(m.target);
                else for (const nodo of m.addedNodes) aplicar(nodo);
            }
        });
        observer.observe(document.body, {
            childList: true, characterData: true, subtree: true, attributes: true,
            attributeFilter: ["aria-label", "placeholder", "title"]
        });
    }

    window.LVLI18n = Object.freeze({
        iniciar: iniciar,
        establecerIdioma: establecerIdioma,
        idioma: function () { return idioma; },
        t: traducirFrase,
        aplicar: aplicar
    });
})();
