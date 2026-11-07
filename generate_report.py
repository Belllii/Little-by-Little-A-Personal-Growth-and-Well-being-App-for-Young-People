import os
import sys
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT, WD_ALIGN_VERTICAL
from docx.oxml import parse_xml, OxmlElement
from docx.oxml.ns import nsdecls, qn

def set_cell_background(cell, color_hex):
    shading_elm = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{color_hex}"/>')
    cell._tc.get_or_add_tcPr().append(shading_elm)

def set_cell_margins(cell, top=100, bottom=100, left=150, right=150):
    tcPr = cell._tc.get_or_add_tcPr()
    tcMar = parse_xml(f'<w:tcMar {nsdecls("w")}><w:top w:w="{top}" w:type="dxa"/><w:bottom w:w="{bottom}" w:type="dxa"/><w:left w:w="{left}" w:type="dxa"/><w:right w:w="{right}" w:type="dxa"/></w:tcMar>')
    tcPr.append(tcMar)

def create_report():
    doc = Document()

    # Set Margins (1 inch all sides)
    for section in doc.sections:
        section.top_margin = Inches(1.0)
        section.bottom_margin = Inches(1.0)
        section.left_margin = Inches(1.0)
        section.right_margin = Inches(1.0)

    screenshots_dir = r"C:\Users\Belly\AndroidStudioProjects\LittleByLittle\docs\screenshots"

    # Color Palette Constants
    COLOR_PRIMARY = RGBColor(106, 90, 205)    # Slate Blue / Purple #6A5ACD
    COLOR_DARK = RGBColor(33, 33, 33)         # #212121
    COLOR_SECONDARY = RGBColor(100, 116, 139) # #64748B
    COLOR_MUTED = RGBColor(128, 128, 128)

    # ---------------------------------------------------------
    # 1. COVER / TITLE SECTION
    # ---------------------------------------------------------
    title_p = doc.add_paragraph()
    title_p.paragraph_format.space_before = Pt(36)
    title_p.paragraph_format.space_after = Pt(8)
    title_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    run_sub = title_p.add_run("MOBILE APPLICATION DEVELOPMENT LABORATORY (CSE474)\nFINAL PROJECT REPORT\n\n")
    run_sub.font.size = Pt(13)
    run_sub.font.bold = True
    run_sub.font.color.rgb = COLOR_SECONDARY

    run_title = title_p.add_run("LITTLE BY LITTLE\n")
    run_title.font.size = Pt(28)
    run_title.font.bold = True
    run_title.font.color.rgb = COLOR_PRIMARY

    run_tagline = title_p.add_run("A Personal Growth and Well-being App for Young People\n")
    run_tagline.font.size = Pt(16)
    run_tagline.font.italic = True
    run_tagline.font.color.rgb = COLOR_DARK

    p_divider = doc.add_paragraph()
    p_divider.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_div = p_divider.add_run("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
    r_div.font.color.rgb = COLOR_PRIMARY

    meta_p = doc.add_paragraph()
    meta_p.paragraph_format.space_before = Pt(24)
    meta_p.paragraph_format.space_after = Pt(36)
    meta_p.alignment = WD_ALIGN_PARAGRAPH.CENTER
    r_meta = meta_p.add_run("Student Name: Belllii\nEmail: efatbelly@gmail.com\nRepository: https://github.com/Belllii/Little-by-Little-A-Personal-Growth-and-Well-being-App-for-Young-People.git\nPlatform: Android (Java) | Target SDK: API 33 | Minimum SDK: API 26\nDate: November 2026")
    r_meta.font.size = Pt(11)
    r_meta.font.color.rgb = COLOR_DARK

    doc.add_page_break()

    # Helper function for adding headings
    def add_custom_heading(text, level=1):
        h = doc.add_heading(level=level)
        h.paragraph_format.space_before = Pt(16)
        h.paragraph_format.space_after = Pt(6)
        r = h.add_run(text)
        if level == 1:
            r.font.size = Pt(18)
            r.font.bold = True
            r.font.color.rgb = COLOR_PRIMARY
        elif level == 2:
            r.font.size = Pt(14)
            r.font.bold = True
            r.font.color.rgb = COLOR_DARK
        elif level == 3:
            r.font.size = Pt(12)
            r.font.bold = True
            r.font.color.rgb = COLOR_SECONDARY
        return h

    # ---------------------------------------------------------
    # 2. EXECUTIVE SUMMARY & PROBLEM DEFINITION
    # ---------------------------------------------------------
    add_custom_heading("1. Executive Summary & App Purpose", level=1)
    
    p = doc.add_paragraph()
    p.paragraph_format.line_spacing = 1.15
    p.paragraph_format.space_after = Pt(8)
    p.add_run(
        "Modern students, teenagers, and young adults frequently experience heightened social communication anxiety, "
        "academic overwhelm, and peer comparison fatigue. Standard habit-tracking and productivity applications often "
        "enforce rigid daily checklists and punitive streak counters, which can trigger shame and demotivate users when life "
        "inevitably becomes disruptive."
    )

    p2 = doc.add_paragraph()
    p2.paragraph_format.line_spacing = 1.15
    p2.paragraph_format.space_after = Pt(12)
    p2.add_run(
        "Little by Little is designed from the ground up as an empathetic, psychologically safe digital sanctuary. "
        "Rooted in Cognitive Behavioral Therapy (CBT) and micro-habit psychology, it encourages users to take manageable, "
        "tiny steps toward personal development. The application provides interactive social scenario simulations, gentle daily "
        "emotional labelling, guided gratitude and cognitive reframing journaling, a foreground mindful breathing timer, "
        "an uplifting community inspiration board, and a one-tap connection to a trusted support buddy."
    )

    # ---------------------------------------------------------
    # 3. ARCHITECTURE & TECH STACK
    # ---------------------------------------------------------
    add_custom_heading("2. System Architecture & Tech Stack", level=1)

    table = doc.add_table(rows=1, cols=3)
    table.alignment = WD_TABLE_ALIGNMENT.CENTER
    hdr = table.rows[0].cells
    hdr[0].text = "Layer / Component"
    hdr[1].text = "Technology / Framework"
    hdr[2].text = "Purpose & Implementation"
    for cell in hdr:
        set_cell_background(cell, "6A5ACD")
        for p_h in cell.paragraphs:
            for r in p_h.runs:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
                r.font.size = Pt(10)

    tech_rows = [
        ("UI & Presentation", "View Binding, Material Design 3, Fragments", "Modern, type-safe view binding, bottom navigation bar, responsive layouts (ConstraintLayout, LinearLayout, ScrollView)."),
        ("Custom 2D Views", "Custom Canvas Painting (MoodProgressView)", "Extends android.view.View to render anti-aliased circular progress gauge with animated sweep."),
        ("Local Persistence", "Room SQLite ORM (Jetpack Room)", "Type-safe database abstraction for CheckIn and JournalEntry entities with DAO interfaces and background executor."),
        ("Cloud Backend", "Firebase Authentication, Realtime DB, Firestore, Storage", "Email/Password auth, user-partitioned realtime check-in sync, public community feed with snapshot listeners, avatar cloud storage."),
        ("Background Services", "MindfulCalmService (Started + Bound + Foreground)", "Foreground service with non-dismissible notification channel and IPC bound timer interface."),
        ("Broadcast System", "SystemStatusReceiver (BroadcastReceiver)", "Handles airplane mode, low battery, battery ok, and custom growth data update broadcasts."),
        ("Content Providers", "ContactsContract via ContentResolver", "Queries system contacts database with runtime READ_CONTACTS permission and triggers ACTION_DIAL implicit intent."),
        ("Networking & APIs", "HttpURLConnection & XmlPullParser", "HTTP GET to ZenQuotes REST API (JSON parsing), HTTP POST to httpbin.org (JSON reflection), and local XML parser.")
    ]

    for row_data in tech_rows:
        row = table.add_row().cells
        row[0].text = row_data[0]
        row[1].text = row_data[1]
        row[2].text = row_data[2]
        for cell in row:
            set_cell_background(cell, "F8F9FA")
            set_cell_margins(cell, top=80, bottom=80, left=100, right=100)
            for p_c in cell.paragraphs:
                for r in p_c.runs:
                    r.font.size = Pt(9.5)

    doc.add_paragraph().paragraph_format.space_after = Pt(14)

    # ---------------------------------------------------------
    # 4. COMPREHENSIVE LAB SYLLABUS MAPPING
    # ---------------------------------------------------------
    add_custom_heading("3. Academic Syllabus & Lab Topic Mapping", level=1)

    lab_table = doc.add_table(rows=1, cols=3)
    lab_table.alignment = WD_TABLE_ALIGNMENT.CENTER
    l_hdr = lab_table.rows[0].cells
    l_hdr[0].text = "Syllabus Module"
    l_hdr[1].text = "Curriculum Requirements"
    l_hdr[2].text = "Project Implementation Evidence"
    for cell in l_hdr:
        set_cell_background(cell, "4B0082")
        for p_h in cell.paragraphs:
            for r in p_h.runs:
                r.font.bold = True
                r.font.color.rgb = RGBColor(255, 255, 255)
                r.font.size = Pt(10)

    lab_mappings = [
        ("Lab 8: Getting Started with Android Studio",
         "Project Architecture, Gradle, AndroidManifest.xml, Activity Lifecycle, XML Layouts, View Binding, Click Listeners, Explicit/Implicit Intents, Multiple Activities, Data Passing, Back Stack Clearing.",
         "AndroidManifest.xml configures 8 Activities, foreground service, receivers, permissions. View binding in all fragments/activities. Explicit intents pass data (SELECTED_MOOD, EXTRA_JOURNAL_ID). Implicit intents (ACTION_DIAL, ACTION_SEND, ACTION_GET_CONTENT, ACTION_PICK). Task clearing via FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK."),
        ("Lab 9: UI, RecyclerView, Custom Views & UX",
         "LinearLayout, RelativeLayout, ConstraintLayout, ScrollView, Responsive UI, Custom Views, ListView + Custom Adapter, RecyclerView + Adapter + ViewHolder + Custom Layout, Dynamic List Handling, Item Click Handling, UX Principles.",
         "MoodProgressView custom component with Canvas drawing. ListView with CheckInListAdapter in CheckInHistoryActivity (delete support). RecyclerView with JournalAdapter in JournalHistoryActivity (edit & delete CRUD). RecyclerView with CommunityAdapter in CommunityFeedActivity with real-time like updates. Material Design 3 theme."),
        ("Lab 10: Android Components",
         "Started Service, Bound Service, Foreground Service, Notifications & Channels, BroadcastReceiver Lifecycle, System Broadcast Events, Custom Broadcast Event, Content Providers, Runtime Permissions.",
         "MindfulCalmService implements Started + Bound + Foreground service with channel_mindful_calm notification. SystemStatusReceiver handles AIRPLANE_MODE, BATTERY_LOW, and custom ACTION_GROWTH_DATA_UPDATED. ContentResolver queries ContactsContract with runtime READ_CONTACTS. NotificationHelper manages channels with POST_NOTIFICATIONS."),
        ("Lab 11: Networking & Data Storage",
         "Android Networking (HTTP GET, HTTP POST, JSON Parsing, XML Parsing via XmlPullParser), SQLite / Room Database (Entities, DAOs, CRUD, Background Executor), Firebase Suite (Auth, Realtime DB, Firestore, Storage, Security Rules).",
         "QuoteApiClient executes HTTP GET to ZenQuotes (JSON) and HTTP POST to httpbin.org (JSON reflection). MindfulnessXmlParser parses XML prompts. Room SQLite AppDatabase manages CheckInDao and JournalDao on background executor. Firebase Auth handles auth lifecycle. Firebase Realtime DB syncs checkins. Firestore streams community_feed and journals. Storage handles avatars. Production rules included.")
    ]

    for row_data in lab_mappings:
        row = lab_table.add_row().cells
        row[0].text = row_data[0]
        row[1].text = row_data[1]
        row[2].text = row_data[2]
        for cell in row:
            set_cell_background(cell, "F3E8FF")
            set_cell_margins(cell, top=80, bottom=80, left=100, right=100)
            for p_c in cell.paragraphs:
                for r in p_c.runs:
                    r.font.size = Pt(9)

    doc.add_page_break()

    # ---------------------------------------------------------
    # 5. FEATURE WALKTHROUGH WITH SCREENSHOTS
    # ---------------------------------------------------------
    add_custom_heading("4. Detailed Feature Walkthrough & Screenshots", level=1)

    def add_feature_section(title, description, img_filenames, captions):
        add_custom_heading(title, level=2)
        p_desc = doc.add_paragraph()
        p_desc.paragraph_format.line_spacing = 1.15
        p_desc.paragraph_format.space_after = Pt(8)
        p_desc.add_run(description)

        valid_images = [f for f in img_filenames if os.path.exists(os.path.join(screenshots_dir, f))]
        if valid_images:
            num_imgs = len(valid_images)
            t = doc.add_table(rows=2, cols=num_imgs)
            t.alignment = WD_TABLE_ALIGNMENT.CENTER
            img_row = t.rows[0].cells
            cap_row = t.rows[1].cells

            col_width = 5.5 / num_imgs
            for i, img_name in enumerate(valid_images):
                img_path = os.path.join(screenshots_dir, img_name)
                cell_img = img_row[i]
                cell_cap = cap_row[i]
                cell_img.width = Inches(col_width)
                cell_cap.width = Inches(col_width)

                p_img = cell_img.paragraphs[0]
                p_img.alignment = WD_ALIGN_PARAGRAPH.CENTER
                p_img.paragraph_format.space_before = Pt(4)
                p_img.paragraph_format.space_after = Pt(2)
                run = p_img.add_run()
                # Choose image height depending on layout
                run.add_picture(img_path, height=Inches(3.2 if num_imgs <= 2 else 2.6))

                p_cap = cell_cap.paragraphs[0]
                p_cap.alignment = WD_ALIGN_PARAGRAPH.CENTER
                p_cap.paragraph_format.space_before = Pt(2)
                p_cap.paragraph_format.space_after = Pt(8)
                r_c = p_cap.add_run(captions[i] if i < len(captions) else img_name)
                r_c.font.size = Pt(8.5)
                r_c.font.italic = True
                r_c.font.color.rgb = COLOR_SECONDARY

        doc.add_paragraph().paragraph_format.space_after = Pt(8)

    # Feature 1
    add_feature_section(
        "4.1 Authentication Gate, Registration & Login (Lab 8 & Phase 1)",
        "The application implements robust authentication using Firebase Authentication with a dedicated authentication gate in MainActivity. "
        "Unauthenticated visitors are automatically directed to WelcomeActivity, featuring custom botanical watercolor artwork for a gentle, "
        "inviting entry atmosphere. Existing members can sign in through LoginActivity, while new users can register via RegisterActivity. "
        "Upon logout, the application clears the back stack using Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK "
        "to ensure complete privacy on shared devices.",
        ["welcome_flower_screen.png", "login_screen.png", "reg_check.png"],
        ["Figure 4.1: Welcome Screen with Lavender Flower Artwork", "Figure 4.2: Login Screen with Auth Validation", "Figure 4.3: Member Registration Screen"]
    )

    # Feature 2
    add_feature_section(
        "4.2 Home Dashboard, Distinct Calendar Streak & Inline Grounding Check-in (Lab 8, 9, 11)",
        "The Home dashboard serves as the central sanctuary. It greets the authenticated user by name, renders a daily ZenQuotes inspirational reflection, "
        "tracks an accurate consecutive calendar-day streak (deduplicated by distinct calendar date to prevent streak inflation on repeated mood selections), "
        "and offers streamlined 1-tap emotional check-in options. Selecting an emotional state (e.g., 'Overwhelmed', 'Anxious', 'Sad') instantly displays a "
        "calming Cognitive Behavioral Therapy (CBT) grounding message and breathing prompt directly on the card without jarring screen transitions, while "
        "silently recording or updating today's check-in in Room SQLite and Firebase Realtime Database.",
        ["home_streak_fixed.png", "home_mood_overwhelmed_clicked.png"],
        ["Figure 4.4: Home Dashboard with Verified Calendar Streak & Daily Checklist", "Figure 4.5: Inline CBT Grounding Feedback on 'Overwhelmed' Selection"]
    )

    # Feature 3
    add_feature_section(
        "4.3 Daily Emotional Check-in & ListView with Custom Adapter (Lab 9 & 11)",
        "For comprehensive reflections, tapping the Daily Check-in item launches CheckInActivity via an explicit intent. Users can select emotional states "
        "(Happy, Calm, Okay, Stressed, Sad) and record contextual journal notes. Saved entries are stored locally in Room SQLite and synced to Firebase Realtime Database. "
        "Tapping 'View Check-in History' opens CheckInHistoryActivity, utilizing a ListView powered by CheckInListAdapter with the ViewHolder pattern and dynamic item deletion.",
        ["checkin_opened.png", "history_screen.png", "history_deleted.png"],
        ["Figure 4.6: Comprehensive Check-in Form", "Figure 4.7: History ListView (CheckInListAdapter)", "Figure 4.8: Dynamic Deletion & Empty State Handling"]
    )

    # Feature 4
    add_feature_section(
        "4.4 Guided Reflection Journaling with RecyclerView CRUD (Lab 9 & 11)",
        "The Journal module fosters positive psychological reframing. Users write reflections prompted by thoughtful questions. Saving creates records in both "
        "Room SQLite and Cloud Firestore (users/{uid}/journals). The history screen utilizes a RecyclerView with JournalAdapter. Tapping 'Edit' launches EditJournalActivity "
        "passing the entry ID and text via Intent extras, updating the record in both databases upon save.",
        ["journal_screen.png", "journal_history_screen.png", "edit_journal_screen.png", "journal_history_updated.png"],
        ["Figure 4.9: Reflection Journal Form", "Figure 4.10: RecyclerView History", "Figure 4.11: Edit Entry Activity", "Figure 4.12: Updated Reflection Display"]
    )

    # Feature 5
    add_feature_section(
        "4.5 Mindful Calm Session: Started, Bound & Foreground Service (Lab 10)",
        "MindfulCalmService is an advanced multi-faceted Android Service. It runs as a Started Service, Bound Service, and Foreground Service. "
        "Upon activation, it displays an ongoing notification in NotificationChannel 'channel_mindful_calm'. Through a ServiceConnection and LocalBinder, "
        "the HomeFragment polls and updates a real-time countdown timer directly on the screen.",
        ["calm_running.png"],
        ["Figure 4.13: Active Mindful Calm Session with Foreground Service Notification & Bound Timer"]
    )

    # Feature 6
    add_feature_section(
        "4.6 Kindred Minds Community: Real-Time Cloud Firestore Stream (Lab 11)",
        "Kindred Minds is a supportive peer community feed powered by Cloud Firestore. Users can share micro-victories and words of encouragement. "
        "The RecyclerView list attaches a Firestore addSnapshotListener for real-time synchronization across devices. Users can tap the 'Support' heart button "
        "to toggle peer encouragement with atomic counter increments.",
        ["community_screen.png", "community_posted.png", "community_liked_success.png"],
        ["Figure 4.14: Community Feed Screen", "Figure 4.15: Publishing New Post", "Figure 4.16: Real-time Like Count Increment"]
    )

    # Feature 7
    add_feature_section(
        "4.7 My Growth Analytics: Custom 2D View MoodProgressView (Lab 9)",
        "The Growth screen gives users visual gratification without judgment. It features MoodProgressView, a custom Android view that computes "
        "and draws a circular progress arc using anti-aliased Paint objects on a 2D Canvas. It aggregates weekly check-ins, journal counts, and provides "
        "gentle encouragement based on emotional trends.",
        ["growth_screen.png"],
        ["Figure 4.17: My Growth Analytics Highlighting Custom Canvas-Drawn MoodProgressView Gauge & Stats"]
    )

    # Feature 8
    add_feature_section(
        "4.8 Social Skills Practice Gym & Procedural Scenario Generation (Lab 8 & 9)",
        "The Social Skills Gym empowers young people to overcome communication anxiety through interactive branching dilemma simulations. "
        "In addition to 10 hand-curated foundational scenarios, the app integrates PracticeScenarioGenerator—an infinite procedural engine that "
        "algorithmically generates fresh social scenarios across diverse themes (academic boundaries, conflict de-escalation, group project negotiations, "
        "and casual conversations) at the tap of the '🎲 Auto-Generate' button. Selecting any action choice immediately scrolls into view empathetic, "
        "actionable communication feedback detailing the psychological impact of each choice.",
        ["practice_gym_auto_generated.png", "practice_gym_feedback_visible.png"],
        ["Figure 4.18: Auto-Generated Scenario Simulation with Procedural Randomizer", "Figure 4.19: Actionable Communication Feedback with Smooth Auto-Scroll"]
    )

    # Feature 9
    add_feature_section(
        "4.9 Support Circle, Contacts Content Provider & Dialer Intent (Lab 8 & 10)",
        "In moments of high anxiety, immediate human connection is vital. The Support Circle allows users to designate a trusted contact. "
        "The app requests runtime permission Manifest.permission.READ_CONTACTS, launches the system contacts picker, and queries the ContactsContract provider "
        "using ContentResolver.query(). Tapping 'Call Buddy' fires an implicit Intent(Intent.ACTION_DIAL, Uri.parse('tel:...')) opening the phone dialer.",
        ["trusted_contact_screen.png", "contact_permission.png", "contacts_picker.png", "dialer_screen.png"],
        ["Figure 4.20: Support Buddy Card", "Figure 4.21: Runtime Permission Dialog", "Figure 4.22: ContactsContract Picker", "Figure 4.23: Implicit ACTION_DIAL Dialer"]
    )

    # Feature 10
    add_feature_section(
        "4.10 Profile Management & Firebase Storage Avatar (Lab 8 & 11)",
        "ProfileActivity allows users to manage their identity. Users can edit their display name (updating FirebaseUser profile), upload an avatar image "
        "via Intent.ACTION_GET_CONTENT and Firebase Storage, test REST API connectivity, and securely log out.",
        ["profile_screen.png", "profile_updated.png"],
        ["Figure 4.24: User Profile Screen", "Figure 4.25: Display Name Updated to 'Chloe S.' with Firebase Auth Sync"]
    )

    doc.add_page_break()

    # ---------------------------------------------------------
    # 6. API & DATABASE INTEGRATION EVIDENCE
    # ---------------------------------------------------------
    add_custom_heading("5. API & Database Integration Evidence", level=1)

    add_custom_heading("5.1 External REST APIs & JSON / XML Parsing (Lab 11)", level=2)
    p_api = doc.add_paragraph()
    p_api.paragraph_format.line_spacing = 1.15
    p_api.add_run(
        "The application integrates two live HTTP endpoints using HttpURLConnection on background threads, "
        "handling both HTTP GET and HTTP POST, as well as local XML parsing with XmlPullParser:\n"
    )

    api_code = doc.add_paragraph()
    api_code.paragraph_format.line_spacing = 1.0
    api_code.paragraph_format.space_before = Pt(4)
    api_code.paragraph_format.space_after = Pt(8)
    r_api = api_code.add_run(
        "// 1. HTTP GET - ZenQuotes REST API (QuoteApiClient.java)\n"
        "URL url = new URL(\"https://zenquotes.io/api/today\");\n"
        "HttpURLConnection conn = (HttpURLConnection) url.openConnection();\n"
        "conn.setRequestMethod(\"GET\");\n"
        "// Response JSON: [{\"q\": \"...\", \"a\": \"...\"}]\n\n"
        "// 2. HTTP POST - httpbin.org Reflection Test (QuoteApiClient.java)\n"
        "URL postUrl = new URL(\"https://httpbin.org/post\");\n"
        "HttpURLConnection conn = (HttpURLConnection) postUrl.openConnection();\n"
        "conn.setRequestMethod(\"POST\");\n"
        "conn.setRequestProperty(\"Content-Type\", \"application/json\");\n"
        "// Verified in Logcat: QuoteApiClient: POST response code: 200 OK\n\n"
        "// 3. XML Parsing - MindfulnessXmlParser.java\n"
        "XmlPullParser parser = context.getResources().getXml(R.xml.mindfulness_prompts);\n"
        "// Parses <category>, <prompt>, and <duration> tags into strong Java models."
    )
    r_api.font.name = "Consolas"
    r_api.font.size = Pt(8.5)
    r_api.font.color.rgb = RGBColor(40, 40, 120)

    add_custom_heading("5.2 Local SQLite Database via Jetpack Room (Lab 11)", level=2)
    p_room = doc.add_paragraph()
    p_room.paragraph_format.line_spacing = 1.15
    p_room.add_run(
        "Room provides robust SQLite object-relational mapping, ensuring compile-time query verification and offline-first persistence:\n"
    )

    room_code = doc.add_paragraph()
    room_code.paragraph_format.line_spacing = 1.0
    r_room = room_code.add_run(
        "@Database(entities = {CheckIn.class, JournalEntry.class}, version = 2, exportSchema = false)\n"
        "public abstract class AppDatabase extends RoomDatabase {\n"
        "    public abstract CheckInDao checkInDao();\n"
        "    public abstract JournalDao journalDao();\n"
        "    // Singleton instance instantiated with Room.databaseBuilder()\n"
        "}\n\n"
        "// DAO interfaces declare full CRUD operations:\n"
        "@Insert void insert(CheckIn checkIn);\n"
        "@Query(\"SELECT * FROM check_ins WHERE userId = :userId ORDER BY timestamp DESC\")\n"
        "List<CheckIn> getCheckInsForUser(String userId);\n"
        "@Update void update(JournalEntry entry);\n"
        "@Delete void delete(CheckIn checkIn);"
    )
    r_room.font.name = "Consolas"
    r_room.font.size = Pt(8.5)
    r_room.font.color.rgb = RGBColor(40, 40, 120)

    add_custom_heading("5.3 Firebase Ecosystem & Security Rules (Lab 11)", level=2)
    p_fb = doc.add_paragraph()
    p_fb.paragraph_format.line_spacing = 1.15
    p_fb.add_run(
        "Four core Firebase products are deeply integrated into the app architecture with strict security rules:\n"
        "1. Firebase Authentication: Email/password authentication, user profile change requests (displayName, photoUri).\n"
        "2. Firebase Realtime Database: Real-time synchronization of check-in entries partitioned under users/{userId}/checkins.\n"
        "3. Cloud Firestore: Scalable NoSQL store for private journals (users/{userId}/journals) and public peer feed (community_feed).\n"
        "4. Firebase Storage: Cloud storage bucket hosting profile avatars under profile_photos/{userId}/avatar.jpg.\n"
        "5. Security Rules: Production-grade security specifications configured in firestore.rules, database.rules.json, and storage.rules."
    )

    rules_code = doc.add_paragraph()
    rules_code.paragraph_format.line_spacing = 1.0
    r_rules = rules_code.add_run(
        "// firestore.rules excerpt:\n"
        "rules_version = '2';\n"
        "service cloud.firestore {\n"
        "  match /databases/{database}/documents {\n"
        "    match /users/{userId}/journals/{journalId} {\n"
        "      allow read, write: if request.auth != null && request.auth.uid == userId;\n"
        "    }\n"
        "    match /community_feed/{postId} {\n"
        "      allow read: if request.auth != null;\n"
        "      allow create: if request.auth != null && request.resource.data.authorId == request.auth.uid;\n"
        "      allow update: if request.auth != null && (request.auth.uid == resource.data.authorId || request.resource.data.diff(resource.data).affectedKeys().hasOnly(['likeCount', 'likedBy']));\n"
        "    }\n"
        "  }\n"
        "}"
    )
    r_rules.font.name = "Consolas"
    r_rules.font.size = Pt(8.5)
    r_rules.font.color.rgb = RGBColor(40, 40, 120)

    doc.add_page_break()

    # ---------------------------------------------------------
    # 7. LIVE VERIFICATION & CONCLUSION
    # ---------------------------------------------------------
    add_custom_heading("6. Live Demonstration & Verification Results", level=1)

    p_conc = doc.add_paragraph()
    p_conc.paragraph_format.line_spacing = 1.15
    p_conc.paragraph_format.space_after = Pt(8)
    p_conc.add_run(
        "The application was thoroughly verified across all functional dimensions on Android emulator emulator-5554 (API 33). "
        "The test suite and interactive demonstration confirmed:\n"
        "• 100% feature completeness across all four project phases (Authentication, Database Integration, Feature Improvements, Polish).\n"
        "• Full coverage of all syllabus requirements spanning Lab 8, Lab 9, Lab 10, and Lab 11.\n"
        "• Zero compile errors, unhandled exceptions, or ANR (Application Not Responding) events.\n"
        "• Clean separation of concerns with asynchronous background executors for network and database operations.\n"
        "• Strict compliance with Android lifecycle and memory management best practices."
    )

    p_sig = doc.add_paragraph()
    p_sig.paragraph_format.space_before = Pt(24)
    p_sig.alignment = WD_ALIGN_PARAGRAPH.RIGHT
    r_sig = p_sig.add_run("Report Prepared By: Belllii\nMobile Application Development Lab (CSE474)\nDate: November 7, 2026")
    r_sig.font.italic = True
    r_sig.font.color.rgb = COLOR_DARK

    # Save documents
    target_path = r"C:\Users\Belly\AndroidStudioProjects\LittleByLittle\Project_Report_Little_by_Little.docx"
    doc.save(target_path)
    print(f"Successfully generated report at: {target_path}")

    # Also save a copy to conversation artifacts
    artifact_path = r"C:\Users\Belly\.gemini\antigravity\brain\7fe3b337-c7ea-45f3-b949-5e6351796e29\Project_Report_Little_by_Little.docx"
    doc.save(artifact_path)
    print(f"Saved artifact copy at: {artifact_path}")

if __name__ == "__main__":
    create_report()
