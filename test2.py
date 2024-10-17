import pygame

class Snake(pygame.sprite.Sprite):
  def __init__(self):
    super().__init__()
    self.image = pygame.image.load("snake.png")
    self.rect = self.image.get_rect()
    self.direction = pygame.Vector2(0, 0)

  def update(self):
    self.rect.move_ip(self.direction)

class Fruit(pygame.sprite.Sprite):
  def __init__(self):
    super().__init__()
    self.image = pygame.image.load("fruit.png")
    self.rect = self.image.get_rect()

def main():
  pygame.init()
  screen = pygame.display.set_mode((640, 480))
  clock = pygame.time.Clock()

  snake = Snake()
  fruit = Fruit()

  all_sprites = pygame.sprite.Group()
  all_sprites.add(snake)
  all_sprites.add(fruit)

  while True:
    for event in pygame.event.get():
      if event.type == pygame.QUIT:
        pygame.quit()
        exit()

      if event.type == pygame.KEYDOWN:
        if event.key == pygame.K_UP:
          snake.direction.y = -1
        elif event.key == pygame.K_DOWN:
          snake.direction.y = 1
        elif event.key == pygame.K_LEFT:
          snake.direction.x = -1
        elif event.key == pygame.K_RIGHT:
          snake.direction.x = 1

    screen.fill((0, 0, 0))
    all_sprites.update()
    all_sprites.draw(screen)

    pygame.display.flip()
    clock.tick(60)

if __name__ == "__main__":
  main()

