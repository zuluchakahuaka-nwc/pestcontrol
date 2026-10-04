# ДезКонтроль / PestControl

**RU** | [EN](#english) | [Հայերեն](#հայերեն) | [Português](#português) | [Español](#español) | [Français](#français) | [Deutsch](#deutsch) | [हिन्दी](#हिन्दी) | [中文](#中文) | [العربية](#العربية)

---

## Русский

**ДезКонтроль** — Android-приложение для дезинсекторов и организаций по контролю вредителей: расчёт средств борьбы, учёт ловушек, планы объектов.

### Что делает

- **Объекты** — список объектов (название, адрес, число этажей и ловушек); на каждом — свой многоэтажный план, нарисованный пальцем: контуры, подписи ориентиров (кухня, склад, вход), маркеры.
- **Расчёт** — выбираете вредителя (тараканы / крысы / мыши), уровень заражённости и площадь — приложение считает количество геля или приманки, число точек раскладки, объём рабочего раствора, число клеевых ловушек и контейнеров.
- **Ловушки** — единая база ловушек по всем объектам: фильтры по типу и статусу (Активна / На замене / Снята), заметка и дата установки.

### Для чего

Специалисту на выезде — не держать таблицы и нормы в голове: расчёт дозировки за секунды, ловушки и планы объектов всегда с собой. Данные хранятся локально (JSON), интернет не нужен.

### Сборка

```bat
gradlew.bat :pestcontrol:assembleDebug
```

APK: `pestcontrol\build\outputs\apk\debug\pestcontrol-debug.apk` (Android 7.0+, SDK 34).

---

## English

**PestControl (DezKontrol)** — an Android app for pest control operators: dosage calculation, trap tracking, site plans.

### What it does

- **Sites** — a list of sites (name, address, floors, traps); each has a multi-floor plan drawn with your finger: outlines, labels (kitchen, storage, entrance), markers.
- **Calculation** — pick the pest (cockroaches / rats / mice), infestation level and area — the app computes gel or bait amount, number of placement points, working solution volume, glue traps and containers.
- **Traps** — one database of traps across all sites: filters by type and status (Active / For replacement / Removed), note and installation date.

### What it is for

For field specialists: no spreadsheets or norms to memorize — dosage in seconds, traps and plans always with you. Data is stored locally (JSON), no internet required.

### Build

```bat
gradlew.bat :pestcontrol:assembleDebug
```

APK: `pestcontrol\build\outputs\apk\debug\pestcontrol-debug.apk` (Android 7.0+, SDK 34).

---

## Հայերեն

**ԴեզԿontrol (PestControl)** — Android հավելված միջատազերծողների համար. դեղաչափի հաշվարկ, ծուղակների հաշվառում, օբյեկտի պլաններ։

### Ինչ է անում

- **Օբյեկտներ** — ցանկ (անուն, հասցե, հարկեր, ծուղակներ). յուրաքանչյուրում՝ մատներով գծված բազմահարկ պլան՝ ուրվագծեր, նշումներ (խոհանոց, պահեստ, մուտք), մարկերներ։
- **Հաշվարկ** — ընտրում եք վնասատուին (խարտեզ / առնետ / մուկ), վարակման մակարդակը և մակերեսը — հավելվածը հաշվում է գելի կամ խայծի քանակը, դնելու կետերը, լուծույթի ծավալը, կպչուն ծուղակները։
- **Ծուղակներ** — մեկ բազա բոլոր օբյեկտների համար. ֆիլտրեր տիպով և կարգավիճակով, նշում և տեղադրման ամսաթիվ։

### Ինչի համար է

Դաշտային մասնագետի համար. դեղաչափը վայրկյաններում, ծուղակներն ու պլանները միշտ ձեզ հետ։ Տվյալները տեղում են (JSON), ինտերնետ պետք չէ։

---

## Português

**PestControl (DezKontrol)** — app Android para controladores de pragas: cálculo de dosagem, monitoração de armadilhas, plantas de locais.

### O que faz

- **Locais** — lista de locais (nome, endereço, andares, armadilhas); cada um com planta de vários andares desenhada com o dedo.
- **Cálculo** — escolha a praga (baratas / ratos / camundongos), nível de infestação e área — o app calcula quantidade de gel ou isca, pontos de aplicação, volume de solução, armadilhas de cola.
- **Armadilhas** — banco único com filtros por tipo e status, nota e data de instalação.

### Para quê

Para o técnico em campo: dosagem em segundos, armadilhas e plantas sempre à mão. Dados locais (JSON), sem internet.

---

## Español

**PestControl (DezKontrol)** — aplicación Android para control de plagas: cálculo de dosis, seguimiento de trampas, planos.

### Qué hace

- **Sitios** — lista de sitios (nombre, dirección, pisos, trampas); cada uno con plano multiplanta dibujado con el dedo.
- **Cálculo** — elige la plaga (cucarachas / ratas / ratones), nivel de infestación y área — la app calcula gel o cebo, puntos de colocación, volumen de solución, trampas de cola.
- **Trampas** — base única con filtros por tipo y estado, nota y fecha de instalación.

### Para qué

Para el técnico de campo: dosis en segundos, trampas y planos siempre a mano. Datos locales (JSON), sin internet.

---

## Français

**PestControl (DezKontrol)** — application Android de lutte antiparasitaire : calcul de dose, suivi des pièges, plans.

### Ce qu'elle fait

- **Sites** — liste des sites (nom, adresse, étages, pièges) ; chacun avec un plan multi-étages dessiné au doigt.
- **Calcul** — choisissez le nuisible (blattes / rats / souris), niveau d'infestation et surface — l'app calcule gel ou appât, points de pose, volume de solution, pièges à colle.
- **Pièges** — base unique avec filtres par type et statut, note et date de pose.

### Pour quoi

Pour le technicien de terrain : dose en secondes, pièges et plans toujours à portée. Données locales (JSON), sans internet.

---

## Deutsch

**PestControl (DezKontrol)** — Android-App für Schädlingsbekämpfer: Dosierung, Fallenverwaltung, Pläne.

### Was sie tut

- **Objekte** — Liste der Objekte (Name, Adresse, Etagen, Fallen); jedes mit mehrstöckigem Finger-gezeichneten Plan.
- **Berechnung** — Schädling wählen (Küchenschaben / Ratten / Mäuse), Befallsgrad und Fläche — die App berechnet Gel oder Köder, Auslegepunkte, Lösungsmenge, Klebefallen.
- **Fallen** — eine Datenbank mit Filtern nach Typ und Status, Notiz und Datum.

### Wofür

Für den Außendienst: Dosierung in Sekunden, Fallen und Pläne immer dabei. Lokale Daten (JSON), kein Internet.

---

## हिन्दी

**PestControl (DezKontrol)** — कीट नियंत्रण तकनीशियनों के लिए Android ऐप: खुराक गणना, ट्रैप ट्रैकिंग, साइट प्लान।

### क्या करता है

- **साइटें** — साइटों की सूची (नाम, पता, मंज़िलें, ट्रैप); हर एक में उंगली से बना बहु-मंज़िला प्लान।
- **गणना** — कीट चुनें (कॉकरोच / चूहे / मूस), संक्रमण स्तर और क्षेत्रफल — ऐप जेल या चारा, रखने के बिंदु, घोल की मात्रा, गोंद ट्रैप की संख्या निकालता है।
- **ट्रैप** — सभी साइटों का एक डेटाबेस: प्रकार और स्थिति के फ़िल्टर, नोट और लगाने की तारीख।

### किसलिए

फ़ील्ड तकनीशियन के लिए: सेकंडों में खुराक, ट्रैप और प्लान हमेशा साथ। डेटा स्थानीय (JSON), इंटरनेट नहीं।

---

## 中文

**PestControl（ДезКонтроль）** — 面向有害生物防治技术员的 Android 应用：剂量计算、诱捕器管理、场所平面图。

### 功能

- **场所** — 场所列表（名称、地址、楼层、诱捕器）；每个场所都有用手指绘制的多层平面图。
- **计算** — 选择有害生物（蟑螂/大鼠/小鼠）、侵害程度和面积——应用计算胶饵或毒饵用量、投放点数、药液体积、粘捕板数量。
- **诱捕器** — 所有场所的统一数据库：按类型和状态筛选，备注和安装日期。

### 用途

供现场技术员使用：几秒算出剂量，平面图和诱捕器随身携带。数据本地存储（JSON），无需联网。

---

## العربية

**PestControl (DezKontrol)** — تطبيق أندرويد لمكافحة الآفات: حساب الجرعة، تتبع المصائد، مخططات المواقع.

### ماذا يفعل

- **المواقع** — قائمة المواقع (الاسم، العنوان، الطوابق، المصائد)؛ لكل موقع مخطط متعدد الطوابق مرسوم بالإصبع.
- **الحساب** — اختر الآفة (صراصير / جرذان / فئران)، مستوى الإصابة والمساحة — يحسب التطبيق كمية الجل أو الطُعم، نقاط الوضع، حجم المحلول، المصائد اللاصقة.
- **المصائد** — قاعدة واحدة بفلاتر النوع والحالة، ملاحظة وتاريخ التركيب.

### لماذا

للفني الميداني: الجرعة في ثوانٍ، المصائد والمخططات دائمًا معك. بيانات محلية (JSON)، دون إنترنت.
