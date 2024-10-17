import pygame
class Snake(pygame.sprite.Sprite):
  def __init__(self):
    super().__init__() 
    self.image = pygame.image.load("snake.png")
    self.image = pygame.image.load("fruit.png")
    self.rect = self.image.get_rect()
    self.direction = pygame.Vector2(0, 0)

def main():
    pygame.init()
  screen = pygame.display.set_mode((640, 480))
  clock = pygame.time.Clock()

  snake = Snake()
  all_sprites = pygame.sprite.Group()
  all_sprites.add(snake)
       screen.fill((0, 0, 0))
    all_sprites.update()
    all_sprites.draw(screen)
    pygame.display.flip()
    clock.tick(60)

if __name__ == "__main__":
  main()