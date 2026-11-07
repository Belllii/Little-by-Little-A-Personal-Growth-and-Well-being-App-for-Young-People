package com.example.littlebylittle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PracticeScenarioGenerator {

    private static final List<PracticeScenario> BASE_SCENARIOS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        // Scenario 1: Starting a conversation
        BASE_SCENARIOS.add(new PracticeScenario(
                1,
                "Starting a conversation",
                "Social Icebreaker",
                "You are sitting beside a classmate before class begins. You would like to start a friendly conversation. What could you say?",
                new String[]{
                        "Hi! How is your day going so far?",
                        "Wait quietly and hope they start speaking first.",
                        "Ask several intense personal questions right away."
                },
                new String[]{
                        "A friendly, open-ended question is a natural way to break the ice without adding social pressure.",
                        "Waiting is safe, but taking a tiny initiative builds your social courage muscle over time.",
                        "Starting with deeply personal topics might feel intrusive. Casual, light topics build trust first."
                },
                false
        ));

        // Scenario 2: Joining a group conversation
        BASE_SCENARIOS.add(new PracticeScenario(
                2,
                "Joining a group conversation",
                "Group Dynamics",
                "A circle of peers in the student lounge are discussing a movie or series you recently watched. What could you do to join in?",
                new String[]{
                        "Listen for a brief moment, then chime in with a related question or thought.",
                        "Interrupt suddenly and redirect the whole topic to something else.",
                        "Assume they already have their clique and walk away feeling left out."
                },
                new String[]{
                        "Observing group rhythm and entering on a shared reference shows situational awareness and warmth.",
                        "Abrupt topic shifts disrupt group rapport. Transitioning from their current topic flows better.",
                        "Social anxiety often invents mind-reading stories. Most casual groups welcome shared enthusiasm."
                },
                false
        ));

        // Scenario 3: Asking someone for help
        BASE_SCENARIOS.add(new PracticeScenario(
                3,
                "Asking someone for help",
                "Academic Communication",
                "You are having trouble understanding a confusing section in an assignment. What could you say to a classmate or TA?",
                new String[]{
                        "Excuse me, could you help clarify this concept for me when you have a minute?",
                        "Pretend you completely understand and turn in incomplete work.",
                        "Demand that someone solve the question for you."
                },
                new String[]{
                        "Asking for clarification shows humility and active learning. Most people feel valued when asked for guidance.",
                        "Masking confusion prevents growth. Vulnerability in learning is a sign of strength, not weakness.",
                        "Respecting others' boundaries and time ensures reciprocal, positive academic relationships."
                },
                false
        ));

        // Scenario 4: Expressing disagreement politely
        BASE_SCENARIOS.add(new PracticeScenario(
                4,
                "Expressing disagreement politely",
                "Conflict Resolution",
                "A friend or group member proposes an idea you strongly disagree with. How could you express your perspective constructively?",
                new String[]{
                        "I see where you're coming from, but have we considered this alternative angle?",
                        "Tell them their suggestion makes no sense and dismiss it entirely.",
                        "Stay silent and agree reluctantly, then feel resentful later."
                },
                new String[]{
                        "Acknowledging their intent while proposing another angle keeps dialogue collaborative rather than combative.",
                        "Harsh dismissiveness provokes defensive walls. Focus on the idea rather than attacking the person.",
                        "Chronic people-pleasing creates hidden resentment. Your perspective has equal right to be heard."
                },
                false
        ));

        // Scenario 5: Setting personal boundaries
        BASE_SCENARIOS.add(new PracticeScenario(
                5,
                "Setting personal boundaries",
                "Healthy Boundaries",
                "An acquaintance asks you to attend an event or do a favour, but you are completely exhausted and need rest. How could you respond?",
                new String[]{
                        "Thank you for thinking of me! I really need to recharge tonight, so I won't be able to make it.",
                        "Say yes even though you are exhausted, then feel burnt out and stressed.",
                        "Ignore their message completely and ghost them for three days."
                },
                new String[]{
                        "Clear, polite honesty protects your well-being while honouring the friendship. 'No' is a complete, valid answer.",
                        "Overcommitting leads to emotional exhaustion. Protecting your energy is a prerequisite for healthy relationships.",
                        "Ghosting breeds anxiety on both sides. A brief, courteous decline takes seconds and preserves respect."
                },
                false
        ));

        // Scenario 6: Returning an incorrect order
        BASE_SCENARIOS.add(new PracticeScenario(
                6,
                "Speaking up about an incorrect order",
                "Everyday Assertiveness",
                "At a busy campus cafe, you ordered an oat milk latte but were handed a dairy drink that you cannot consume. What could you say?",
                new String[]{
                        "Pardon me, I think there was a mix-up with my order — I had requested oat milk. Could we remake it please?",
                        "Drink it anyway or throw it in the trash silently to avoid being a bother.",
                        "Snap at the barista angrily and accuse them of not listening to you."
                },
                new String[]{
                        "Calm, direct assertiveness solves the mistake instantly. Food service staff make honest mistakes and are happy to fix them.",
                        "Sacrificing your health or money out of conflict avoidance reinforces feelings of powerlessness. You deserve what you ordered.",
                        "Mistakes in busy environments are rarely personal. Kindness gets problems solved faster with dignity for everyone."
                },
                false
        ));

        // Scenario 7: Handling awkward silences
        BASE_SCENARIOS.add(new PracticeScenario(
                7,
                "Handling an awkward silence",
                "Social Comfort",
                "You and a friend you haven't seen in a while run out of things to say, and a quiet pause settles in. How do you interpret this?",
                new String[]{
                        "Embrace the pause comfortably, take a sip of water, and ask about their weekend.",
                        "Panic internally, assume you are boring them, and make an awkward excuse to leave.",
                        "Start talking frantically about anything random just to fill every single second."
                },
                new String[]{
                        "Silence is a natural rhythm in human connection, not an emergency. Comfortable silence signals genuine security.",
                        "Catastrophizing quiet moments creates self-fulfilling tension. The other person is often just reflecting too.",
                        "Forced nonstop chatter adds nervous energy. Ground yourself with a breath and let the conversation breathe."
                },
                false
        ));

        // Scenario 8: Giving a sincere compliment
        BASE_SCENARIOS.add(new PracticeScenario(
                8,
                "Giving a sincere compliment",
                "Spreading Warmth",
                "A classmate gave a thoughtful presentation in class and you appreciated their insights. How could you reach out?",
                new String[]{
                        "Hey, I really loved your presentation today! That point about community resilience was so insightful.",
                        "Keep it to yourself because you worry they might think you're weird for saying something.",
                        "Give a backhanded compliment like 'I didn't expect you to be that articulate!'"
                },
                new String[]{
                        "Specific, genuine praise creates an instant moment of validation and warmth. It takes courage and makes someone's day.",
                        "Unexpressed kindness helps no one. People almost universally appreciate feeling noticed and valued.",
                        "Backhanded compliments undermine sincerity. Keep affirmations pure, positive, and unconditional."
                },
                false
        ));

        // Scenario 9: Group project accountability
        BASE_SCENARIOS.add(new PracticeScenario(
                9,
                "Addressing an unresponsive group partner",
                "Teamwork & Responsibility",
                "The deadline for a joint assignment is in 24 hours, and one teammate hasn't submitted their required section. What could you write?",
                new String[]{
                        "Hi Alex, just checking in on section 3. We're finalizing the draft by 6 PM today — let us know if you need any support to finish it up!",
                        "Do their entire section yourself through the night while silently fuming.",
                        "Post in the group chat calling them lazy and threatening to report them immediately."
                },
                new String[]{
                        "A firm deadline with an offer of support addresses the objective issue while giving them an opportunity to step up.",
                        "Martyring yourself builds resentment and enables unaccountability. Clear communication comes before panic.",
                        "Public humiliation creates defensiveness. A direct check-in with a firm boundary is far more effective."
                },
                false
        ));

        // Scenario 10: Declining an invitation without guilt
        BASE_SCENARIOS.add(new PracticeScenario(
                10,
                "Declining an invitation when overwhelmed",
                "Self-Compassion",
                "You are invited to a loud weekend party after an exhausting exam week, but you desperately need quiet rest. What could you say?",
                new String[]{
                        "Sounds like an awesome event! My social battery is at zero after exams so I'm taking a quiet night in, but let's grab coffee next week!",
                        "Make up a convoluted, stressful lie about a fake medical appointment or family crisis.",
                        "Accept the invite, go to the party miserable, and hide in the bathroom all evening."
                },
                new String[]{
                        "Being authentic about your social battery normalizes human limits. Proposing an alternative catch-up affirms the friendship.",
                        "Complicated lies create ongoing anxiety about being caught. Genuine friends respect honest boundaries.",
                        "Ignoring your body's stress signals leads to burnout. Honouring your limits is an act of self-respect."
                },
                false
        ));
    }

    // Contexts, characters, dilemmas, and options for dynamic auto-generation
    private static final String[] CONTEXTS = {
            "at the campus library", "during a study session", "in the dormitory lounge",
            "at a student club meetup", "on the campus shuttle", "at the college dining hall",
            "during a lab collaboration", "at an internship team sync", "while queuing for coffee"
    };

    private static final String[] PEOPLE = {
            "a new lab partner", "a quiet peer you admire", "a fast-talking group member",
            "a professor after lecture", "a friendly barista", "a study group acquaintance",
            "a roommate with different habits", "a senior student mentor"
    };

    private static final String[] DILEMMAS = {
            "shares an opinion that feels factually misleading",
            "plays loud audio without headphones while you are trying to study",
            "asks to borrow your detailed lecture notes right before a midterm",
            "makes a subtle joke that makes you feel self-conscious",
            "speaks over you while you were explaining your perspective",
            "invites you to lead a group presentation you don't feel ready for alone",
            "asks for personal advice on a sensitive topic you aren't qualified to answer",
            "forgets an agreement you both made regarding shared chores or tasks"
    };

    public static PracticeScenario getScenario(int index) {
        if (index < BASE_SCENARIOS.size()) {
            return BASE_SCENARIOS.get(index);
        }
        // Procedurally generate beyond base catalog
        return generateProceduralScenario(index);
    }

    public static PracticeScenario generateRandomScenario() {
        int seed = 100 + RANDOM.nextInt(900);
        return generateProceduralScenario(seed);
    }

    public static PracticeScenario generateProceduralScenario(int index) {
        String context = CONTEXTS[RANDOM.nextInt(CONTEXTS.length)];
        String person = PEOPLE[RANDOM.nextInt(PEOPLE.length)];
        String dilemma = DILEMMAS[RANDOM.nextInt(DILEMMAS.length)];

        String title = "Scenario #" + (index + 1) + ": Navigating Social Situations";
        String category = "✨ Auto-Generated Social Challenge";
        String description = "You are " + context + " and " + person + " " + dilemma + ". How would you navigate this moment?";

        String[] options = new String[]{
                "Speak calmly and address the situation directly with empathy and clarity.",
                "Stay silent, ignore the feeling, and hope the awkwardness passes on its own.",
                "React impulsively with irritation or confront them aggressively."
        };

        String[] feedback = new String[]{
                "Assertive, empathetic communication acknowledges both your feelings and the other person's humanity, leading to constructive outcomes.",
                "Passive avoidance may temporarily dodge discomfort, but it leaves your boundaries unprotected and increases internal stress.",
                "Aggressive reactions trigger defensiveness in others, escalating tension rather than resolving the core issue."
        };

        return new PracticeScenario(index + 1, title, category, description, options, feedback, true);
    }

    public static int getBaseCount() {
        return BASE_SCENARIOS.size();
    }
}
