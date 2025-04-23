#include "EFM51BB3U.h"
#include "EFM51_LCD_I2C.h"

// #define LCD_I2C_BUS_ADDRESS 0X3F
#define LCD_I2C_BUS_ADDRESS 0X27
#define VRX_PIN A1
#define VRY_PIN A0
#define SEL_PIN A2

#define COLUMN(x) ((x) >> 1)
#define ROW(x) ((x) % 2)

#define MONKEY_INITIAL_POSITION 2
#define BANANA_INITIAL_POSITION (MONKEY_INITIAL_POSITION + 28)
#define N_TRAPS 10
#define INITIAL_TIME 30
#define TRAP_INITIAL_POSITION 4

// sprites identifiers
#define MONKEY_SPRITE 1
#define TRAP_SPRITE 2
#define TIGER_SPRITE 3
#define BANANAS_SPRITE 4
#define TRAPPED_SPRITE 5

typedef enum game_status_t {
    TITLE,
    GAMING,
    HAPPY_MONKEY,
    TRAPPED_MONKEY,
    DEAD_MONKEY
} game_status_t;

void start_message();
void reset_traps();
void draw_timer();
void redraw_screen();
void update_game();
void end_message();
bool update_monkey_position(uint8_t *current_position);

// lcd sprites 
const byte code monkey[8] = { 0X0E, 0X0E, 0X04, 0X0E, 0X15, 0X04, 0X0A, 0X11 };
const byte code mon_trapped[8] = { ~0X0E, ~0X0E, ~0X04, ~0X0E, ~0X15, ~0X04, ~0X0A, ~0X11 };
const byte code trap[8] = { 0X1F, 0X1F, 0X1F, 0X1F, 0X1F, 0X1F, 0X1F, 0X1F };
const byte code tiger[8] = { 0X11, 0X0E, 0X11, 0X15, 0X04, 0X11, 0X0A, 0X04 };
const byte code bananas[8] = { 0X15, 0X15, 0X15, 0X00, 0X15, 0X15, 0X15, 0X00 };


// from 2 to 31, evens are up and odds are down
uint8_t monkey_position;
bool is_trapped = false;
// 30 or 31
uint8_t banana_position;
uint8_t traps_positions[N_TRAPS] = { 4, 8, 10, 14, 16, 18, 22, 24, 26, 28 };
// from INITIAL_TIME secs to 0
uint8_t timer;
uint8_t new_traps;
game_status_t game_status;

bool must_redraw;

void setup() {
    // initial lcd and clock configuration
    setSysclk(SYSCLK_49MHz, SYSCLK_DIS_OUT);
    smbLcdInit(LCD_I2C_BUS_ADDRESS,I2C_STANDARD_SPEED,16,2);
    lcdClear();
    lcdHome();
    lcdBacklight(true);
    lcdCreateChar(MONKEY_SPRITE, monkey);
    lcdCreateChar(TRAP_SPRITE, trap);
    lcdCreateChar(TIGER_SPRITE, tiger);
    lcdCreateChar(BANANAS_SPRITE, bananas);
    lcdCreateChar(TRAPPED_SPRITE, mon_trapped);
    lcdHome();

    pwmCcmInit(CAPTURE,CAPTURE,NONE,NONE,NONE,NONE);
    pinMode(SEL_PIN, INPUT);

    srand(millis());
}

void loop() {
    // every game starts in a new loop call
    monkey_position = MONKEY_INITIAL_POSITION;
    banana_position = BANANA_INITIAL_POSITION;
    reset_traps();

    timer = INITIAL_TIME;
    new_traps = 0;
    game_status = TITLE;
    must_redraw = false;

    start_message();
    while(analogRead(SEL_PIN));
    lcdClear();

    redraw_screen();

    // game loop
    while (game_status == GAMING && timer > 0) {
        // redraw screen if needed
        if (must_redraw) redraw_screen();
        must_redraw = false;

        update_game();

        // check if game is over
        game_status = (COLUMN(monkey_position) != COLUMN(banana_position))
            ? game_status
            : monkey_position == banana_position ? HAPPY_MONKEY : DEAD_MONKEY;
        game_status = timer == 0 ? TRAPPED_MONKEY : game_status;
    }

    // display end message
    end_message();
}

// reset traps positions on each game
void reset_traps() {
    const uint8_t trap_offsets[N_TRAPS] = {0, 4, 6, 10, 12, 14, 18, 20, 22, 24};
    uint8_t i = 0;

    for (i = 0; i < N_TRAPS; i++) {
        traps_positions[i] = TRAP_INITIAL_POSITION + trap_offsets[i];
    }
}

void start_message() {
    lcdSetCursor(0, 0);
    lcdPrint(Strn, "  MONKEY TRAPS  ");
    lcdSetCursor(0, 1);
    lcdPrint(Strn, " Press Joystick ");

    game_status = GAMING;
}

void draw_timer() {
    lcdClear();
    lcdSetCursor(0, 0);
    lcdPrint(Uchar, timer / 10);
    lcdSetCursor(0, 1);
    lcdPrint(Uchar, timer % 10);
}

void redraw_screen() {
    uint8_t i;
    
    draw_timer();

    // draw traps
    for (i = 0; i < N_TRAPS; i++) {
        // if monkey gets trapped
        if (monkey_position == traps_positions[i]) {
            is_trapped = true;
            banana_position = BANANA_INITIAL_POSITION + (ROW(monkey_position) == 0 ? 1 : 0);
        }
        else {
            lcdSetCursor(COLUMN(traps_positions[i]), ROW(traps_positions[i]));
            lcdWrite(TRAP_SPRITE);
        }
    }

    // draw monkey
    lcdSetCursor(COLUMN(monkey_position), ROW(monkey_position));
    lcdWrite(is_trapped ? TRAPPED_SPRITE : MONKEY_SPRITE);

    // draw bananas
    lcdSetCursor(COLUMN(banana_position), ROW(banana_position));
    lcdWrite(BANANAS_SPRITE);

    // draw tiger
    lcdSetCursor(COLUMN(banana_position),
        ROW(banana_position) == 0 ? (ROW(banana_position) + 1) : (ROW(banana_position) - 1)
    );
    lcdWrite(TIGER_SPRITE);
}

// update game 
void update_game() {
    uint8_t i = 0;
    uint8_t new_trap = 0;

    if (!is_trapped)
        must_redraw = update_monkey_position(&monkey_position);
        
    delay(125);
    new_traps = (new_traps + 1) % 8;

    // every second we update traps position
    if (new_traps == 0) {
        for (i = 0; i < N_TRAPS; i++) {
            new_trap = rand() % 2;
            traps_positions[i] += ROW(traps_positions[i]) == 0
                ? new_trap
                : -new_trap;
        }

        is_trapped = false;
        timer--;
        must_redraw = true;
    }
}


void end_message() {
    lcdSetCursor(0, 0);
    lcdClear();

    switch (game_status) {
        case HAPPY_MONKEY: {
            lcdPrint(Strn, "   H A P P Y");
            lcdSetCursor(0, 1);
            lcdPrint(Strn, "  M O N K E Y");
        } break;
        case TRAPPED_MONKEY: {
            lcdPrint(Strn, " T R A P P E D");
            lcdSetCursor(0, 1);
            lcdPrint(Strn, "  M O N K E Y");
        } break;
        case DEAD_MONKEY: {
            lcdPrint(Strn, "    D E A D");
            lcdSetCursor(0, 1);
            lcdPrint(Strn, "  M O N K E Y ");
        } break;
        default: {
            lcdPrint(Strn, "this should");
            lcdSetCursor(0, 1);
            lcdPrint(Strn, "not happen");
        } break;
    }

    delay(3000);
    game_status = TITLE;
}

bool update_monkey_position(uint8_t *current_position) {
    uint16_t x_value = analogRead(VRX_PIN);
    uint16_t y_value = analogRead(VRY_PIN);
    bool has_move = false;

    // x axis movement
    // left move
    if (x_value <= 412 && COLUMN(*current_position) != 1) {
        *current_position -= 2;
        has_move = true;
    }
    // right move
    else if (x_value >= 612 && COLUMN(*current_position) != COLUMN(BANANA_INITIAL_POSITION)) {
        *current_position += 2;
        has_move = true;
    }

    // y axis movement
    // up move
    if (y_value >= 612 && ROW(*current_position) == 1) {
        *current_position -= 1;
        has_move = true;
    }
    // down move
    else if (y_value <= 412 && *current_position % 2 == 0) {
        *current_position += 1;
        has_move = true;
    }

    return has_move;
}

